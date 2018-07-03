package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Branch;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BranchRepository extends MongoRepository<Branch, String> {
   @Query("{ 'branchName': ?0, 'project_id': ?1}")
   Branch findBy(String branchName, ObjectId project_id);
}