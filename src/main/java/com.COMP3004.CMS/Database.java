package com.COMP3004.CMS;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface Database extends MongoRepository<User,String> {

    public User findByUsername(String username);
    public User findByPassword(String password);
    public User findByUsernameAndPassword(String username, String password);

}