package com.mobileenerlytics.controller;

import com.mobileenerlytics.repository.CommitRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/commit")
public class CommitController {
    @Autowired
    private CommitRepository commitRepository;

    @Autowired
    private MongoDatabase mongoDatabase;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitController.class);

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity getCommits(
            @RequestParam(name="count") int count,
            @RequestParam(required=false, name="branchName") String branchName,
            @RequestParam(required=false, name="middleUpdatedMS") long middleUpdatedMS,
            @RequestParam(name="projectId") String projectId) throws Exception {
        final List<Document> result = new ArrayList<>();
        Document query = new Document();
        if (branchName != null) query.append("branchName", branchName);
        if (projectId != null) query.append("project_id", new ObjectId(projectId));

        if (middleUpdatedMS != 0 ) {
            // greater or equals
            query.append("updatedMs", new Document().append("$gt", new Date(middleUpdatedMS)));
            AggregateIterable<Document> aggregate = getDocuments(count / 2, query, new BasicDBObject("updatedMs", 1));
            aggregate.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    result.add(document);
                }
            });
            query.remove("updatedMs");

            // less than
            query.append("updatedMs", new Document().append("$lte", new Date(middleUpdatedMS)));
            aggregate = getDocuments(count / 2, query, new BasicDBObject("updatedMs", -1));
            aggregate.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    result.add(document);
                }
            });
        } else {
            AggregateIterable<Document> aggregate = getDocuments(count, query, new BasicDBObject("updatedMs", -1));
            aggregate.forEach(new Block<Document>() {
                @Override
                public void apply(Document document) {
                    result.add(document);
                }
            });
        }
        if (result == null || result.isEmpty()) return ResponseEntity.noContent().build();
        else return ResponseEntity.ok(result);
    }


    private AggregateIterable<Document> getDocuments(int count, Document query, BasicDBObject sort) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Commit");
        return collection.aggregate(Arrays.asList(
                Aggregates.match(query),
                Aggregates.sort(sort),
                Aggregates.limit(count),
                Aggregates.sort(new BasicDBObject("updatedMs", -1)),
                Aggregates.lookup("Contributor", "contributor_email", "_id", "contributor"),
                Aggregates.lookup("Branch", "branch_id", "_id", "branch"),
                Aggregates.lookup("TestRecord", "testRecords", "_id", "testRecordSet"),
                Aggregates.unwind("$branch"),
                Aggregates.project(Projections.exclude("branch.commits"))
                )
        );
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity deleteCommitById(@PathVariable("id") String id
            , @RequestParam(name="projectId") String projectId)
    {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Commit");
        MongoCollection<Document> testRecordCollection = mongoDatabase.getCollection("TestRecord");
        MongoCollection<Document> branch = mongoDatabase.getCollection("Branch");
        Document oneAndDelete;
        try {
            oneAndDelete = collection.findOneAndDelete(
                    Filters.and(Filters.eq("_id", id), Filters.eq("project_id", projectId)) );

            //tests
            if (oneAndDelete != null
                    && oneAndDelete.get("testRecords") != null) {
                List<Integer> testRecords = (List<Integer>) oneAndDelete.get("testRecords");
                for (int testId : testRecords)
                    testRecordCollection.findOneAndDelete(Filters.eq("_id", testId));
            }

            //branch
            if (oneAndDelete != null
                    && oneAndDelete.get("branch_id") != null) {
                String branchId = (String) oneAndDelete.get("branch_id");
                Bson query = new Document().append("_id", branchId);
                Bson fields = new Document().append("commits", id);
                Bson update = new Document("$pull",fields);
                branch.updateOne(query, update);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }

        if (oneAndDelete == null)
            return ResponseEntity.noContent().build();
        else
            return ResponseEntity.ok(oneAndDelete);

    }

}
