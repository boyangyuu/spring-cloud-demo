package com.mobileenerlytics.controller;

import com.mobileenerlytics.entity.Project;
import com.mobileenerlytics.repository.ProjectRepository;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/projects")
// todo https://spring.io/guides/gs/rest-service/
public class ProjectController {
    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private MongoDatabase mongoDatabase; // ?

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private RestTemplate restTemplate; // consume other api.

    @GetMapping
    public ResponseEntity getProjects(@RequestHeader HttpHeaders headers) {
//        List<String> username = headers.get("username");
        String userName = "Allmusicapps_3Mins";
        MongoCollection<Document> collection = mongoDatabase.getCollection("Project");
        Document filter = new Document("userId", userName);
        List<Document> documents = collection.find(filter).map(document -> {
            ObjectId id = (ObjectId) document.get("_id");
            document.put("_id", id.toHexString());
            return document;
        }).into(new ArrayList<Document>());

        if (documents == null) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(documents);
        }
    }

    @GetMapping(value="/{id}")
    public ResponseEntity getProject(@PathVariable String id) {
        Project project = projectRepository.findById(id).get();
        if (project == null) return ResponseEntity.notFound().build();
        else return ResponseEntity.ok(project);
    }

    @PutMapping(value="/{projectId}/{attribute}")
    public ResponseEntity updateProject(@PathVariable String projectId,
                                  @PathVariable String attribute,
                                  @RequestParam(name="value") String value
                                  ) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Project");
        Document filter = new Document("_id", new ObjectId(projectId)).append(attribute, new Document("$exists", true));
        Document oneAndUpdate = collection.findOneAndUpdate(
                    filter,
                    new Document("$set", new Document(attribute, value)),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
        if (oneAndUpdate == null) return ResponseEntity.badRequest().build();
        else return ResponseEntity.ok(oneAndUpdate);
    }

}
