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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("api/project")
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
    public List<Project> getProjects(@RequestHeader HttpHeaders headers) {
//        List<String> username = headers.get("username");
        String userName = "Allmusicapps_3Mins";
        return projectRepository.findBy(userName);
    }

    @GetMapping(value="/{id}")
    public Project getProject(@PathVariable String id) {
        List<Project> all = projectRepository.findAll();
        System.out.println(all);

        return projectRepository.findById(id).orElse(null); // todo error handling
    }

    @PutMapping(value="/{projectId}/{attribute}")
    public Document updateProject(@PathVariable String projectId,
                                  @PathVariable String attribute,
                                  @RequestParam(required=true, name="value") String value
                                  ) {
        MongoCollection<Document> collection = mongoDatabase.getCollection("Project");
        Document filter = new Document("_id", new ObjectId(projectId)).append(attribute, new Document("$exists", true));
        Document oneAndUpdate = collection.findOneAndUpdate(
                    filter,
                    new Document("$set", new Document(attribute, value)),
                new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)
        );
        return oneAndUpdate;
    }

    private void validateUser(String name) {
//        this.projectRepository.findByFirstName(name);
    }
}
