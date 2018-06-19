package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Branch;
import com.mobileenerlytics.entity.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;

public interface BranchRepository extends MongoRepository<Branch, String> {
   @Query("{ 'branchName': ?0, 'project_id': ?1}")
   Branch findBy(String branchName, String project_id);
}