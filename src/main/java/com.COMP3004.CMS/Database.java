package com.COMP3004.CMS;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface Database extends MongoRepository<User,String> {

    public User findByUsernameAndRole(String username, String role);
    public User findByPassword(String password);
    public User findByUsernameAndPassword(String username, String password);

}