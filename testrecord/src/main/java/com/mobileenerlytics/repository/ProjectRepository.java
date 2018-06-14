package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Demo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Demo, String> {

    public Demo findByFirstName(String firstName);

    public List<Demo> findByLastName(String lastName);
}