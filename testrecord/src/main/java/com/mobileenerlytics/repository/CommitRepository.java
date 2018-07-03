package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Commit;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface CommitRepository extends MongoRepository<Commit, String> {
   @Query("{ 'hash': ?0, 'branch_id': ?1}")
   Commit findBy(String hash, ObjectId branch_id);

}