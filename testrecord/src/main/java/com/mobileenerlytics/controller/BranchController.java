package com.mobileenerlytics.controller;

import com.mobileenerlytics.repository.BranchRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@RestController
@RequestMapping("api/branches")
public class BranchController {
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private MongoDatabase mongoDatabase;

    private static final Logger LOGGER = LoggerFactory.getLogger(BranchController.class);

    @Autowired
    private RestTemplate restTemplate;


    @GetMapping
    public List<Document> getBranches(
            @RequestParam(required=false, name="prefix") String prefix,
            @RequestParam(required=false, name="branchName") String branchName,
            @RequestParam(required=false, name="count") int count,
            @RequestParam(name="projectId") String projectId) {
        try {
            if (prefix == null || prefix.equals("")) prefix = null;
            if (branchName == null && prefix == null) return queryAllBranches();
            if (prefix == null) return queryBranchByName(projectId, branchName, count);
            else return queryBranchByPrefix(projectId, prefix, count);

        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    private List<Document> queryAllBranches() {
        final List<Document> documents = new ArrayList<>();
        MongoCollection<Document> collection = mongoDatabase.getCollection("Branch");
        BasicDBObject sort = new BasicDBObject("updatedMs", -1);
        collection.find().sort(sort).projection(Projections.exclude("commits")).into(documents);
        for (Document doc : documents) {
            doc.append("commits", new ArrayList<>());
            doc.put("_id", ((ObjectId) doc.get("_id")).toHexString());
        }
        return documents;
    }

    public List<Document> queryBranchByPrefix(String projectId, String prefix,  int numOfCommits) {
        Bson query = Filters.regex("branchName", ".*" + prefix + ".*", "i");
        return getBranches(projectId, query, numOfCommits);
    }


    public List<Document> queryBranchByName(String projectId, String branchName, int numOfCommits) {
        BasicDBObject query = new BasicDBObject("branchName", new BasicDBObject("$eq",branchName));
        return getBranches(projectId, query, numOfCommits);
    }

    List<Document> getBranches(String projectId, Bson query, int numOfCommits) {
        final List<Document> branches = new ArrayList<>();

        MongoCollection<Document> collection = mongoDatabase.getCollection("Branch");
        BasicDBObject field_branch = new BasicDBObject("commits", new BasicDBObject("$slice", new Object[]{"$commits", -numOfCommits}))
                .append("updatedMs", 1)
                .append("branchName", 1)
                .append("tests", 1)
                .append("totalTests", 1)
                .append("totalCommits", 1)
                ;

        collection.aggregate(Arrays.asList(
                Aggregates.match(query),
                Aggregates.match(Filters.eq("project_id", new ObjectId(projectId))),
                Aggregates.project(field_branch)
        )).forEach(new Block<Document>() {
            @Override
            public void apply(final Document b) {
                Set<String> testNamesOfBranch = new HashSet<>();
                List<Document> objects = new ArrayList<>();
                List<ObjectId> commitIds  = (List<ObjectId>) b.get("commits");
                if (commitIds != null) {
                    for (ObjectId cid : commitIds) {
                        Document document = queryCommitByCommitId(cid);
                        document.put("_id", ((ObjectId) document.get("_id")).toHexString());
                        if (document != null) objects.add(0, document);

                        // keep it
                        testNamesOfBranch.addAll((Set<? extends String>) document.get("tests"));
                    }
                }
                b.remove("commits");
                b.append("commits", objects);
                b.append("testsFiltered", testNamesOfBranch);
                b.put("_id", ((ObjectId) b.get("_id")).toHexString());
                branches.add(b);
            }
        });
        return branches;
    }

    public Document queryCommitByCommitId(ObjectId commitId) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Commit");
        BasicDBObject query = new BasicDBObject("_id", new BasicDBObject("$eq", commitId));
        Document document =  collection.aggregate(Arrays.asList(
                Aggregates.match(query),
                Aggregates.lookup("TestRecord", "testRecords", "_id", "testRecordSet"),
                Aggregates.lookup("Contributor", "contributor_email", "_id", "contributor"),
                Aggregates.project(Projections.exclude("testRecordSet.threadCompEnergies"))
        )).first();
        if (document == null) return null;
        List<Document> tests = (List<Document>) document.get("testRecordSet");
        Set<String> testNames = new HashSet<>();
        for (Document testRecord : tests)
            testNames.add(testRecord.getString("testName"));
        document.append("tests", testNames);
        return document;
    }

}
