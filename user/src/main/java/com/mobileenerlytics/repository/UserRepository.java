package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface UserRepository extends MongoRepository<User, String> {

    @Query("{ 'email': ?0}")
    User findUserByEmail(String email);
    @Query("{ 'username': ?0}")
    User findUserByUsername(String username);


}