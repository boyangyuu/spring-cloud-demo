package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Contributor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ContributorRepository extends MongoRepository<Contributor, String> {
   @Query("{ '_id': ?0, 'name': ?1}")
   Contributor findBy(String id, String name);
}