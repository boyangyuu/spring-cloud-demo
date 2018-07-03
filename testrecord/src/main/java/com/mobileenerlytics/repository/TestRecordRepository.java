package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.TestRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TestRecordRepository extends MongoRepository<TestRecord, Integer> {
   @Query("{ 'testName': ?0, 'commit_id': ?1}")
   TestRecord findBy(String testName, ObjectId commit_id);
}