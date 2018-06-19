package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Branch;
import com.mobileenerlytics.entity.Commit;
import com.mobileenerlytics.entity.Contributor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;

public interface CommitRepository extends MongoRepository<Commit, String> {
   @Query("{ 'hash': ?0, 'branch_id': ?1}")
   Commit findBy(String hash, String branch_id);

}