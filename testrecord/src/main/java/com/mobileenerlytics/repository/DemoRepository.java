package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Demo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


//todo add testing  https://github.com/spring-guides/gs-accessing-data-mongodb/blob/master/complete/src/test/java/hello/CustomerRepositoryTests.java
public interface DemoRepository extends MongoRepository<Demo, String> {

    public Demo findByFirstName(String firstName);

    public List<Demo> findByLastName(String lastName);
}