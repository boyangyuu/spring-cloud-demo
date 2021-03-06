package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;


//todo add testing  https://github.com/spring-guides/gs-accessing-data-mongodb/blob/master/complete/src/test/java/hello/CustomerRepositoryTests.java
public interface ProjectRepository extends MongoRepository<Project, String> {
//   @Autowired
//   MongoOperations mongoOperations; //https://stackoverflow.com/questions/23657661/spring-data-mongodb-no-property-get-found-for-type-at-org-springframework-data-m
   @Query("{ 'name': ?0, 'userId': ?1}")
   Project findBy(String name, String userId);

   @Query("{ 'userId': ?0}")
   List<Project> findBy(String userId);
}