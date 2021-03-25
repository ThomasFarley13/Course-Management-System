package com.COMP3004.CMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface Database extends MongoRepository<User,String> {

    public User findByUsername(String username);
    public User findByUsernameAndRole(String username, String role);
    public User findByUsernameAndPassword(String username, String password);
    public User findTopByOrderByIdDesc();
    public List<User> findByActiveIsFalse();
}