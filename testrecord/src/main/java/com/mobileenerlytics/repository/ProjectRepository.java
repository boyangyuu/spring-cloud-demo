package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.*;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;



//todo add testing  https://github.com/spring-guides/gs-accessing-data-mongodb/blob/master/complete/src/test/java/hello/CustomerRepositoryTests.java
public interface ProjectRepository extends MongoRepository<Project, String> {
//   @Autowired
//   MongoOperations mongoOperations; https://stackoverflow.com/questions/23657661/spring-data-mongodb-no-property-get-found-for-type-at-org-springframework-data-m
   Project findProjectBy(String name, String userId);
//   Project find(ObjectId id);
}