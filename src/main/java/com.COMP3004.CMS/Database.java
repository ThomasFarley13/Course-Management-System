package com.COMP3004.CMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

import java.util.List;

interface UserDatabase extends MongoRepository<User,String> {

    public User findByUsername(String username);
    public User findByUsernameAndRole(String username, String role);
    public User findByUsernameAndPassword(String username, String password);
    public User findTopByOrderByIdDesc();
    public List<User> findByActiveIsFalse();
}


interface CourseDatabase extends MongoRepository<Course,String> {
    public Course findCourseByCourseCode (String courseCode);
    public List<Course> findBycoursenumber (int coursenum);
    public List<Course> findByCoursenumberAndCourseDept (int coursenum,String courseDept);
    public List<Course> findByCourselevelAndCourseDept (int courselevel,String courseDept);
    public List<Course> findByCoursenumberAndCourselevelAndCourseDept (int coursenum, int courselevel,String courseDept);
    public List<Course> findByCourselevel (int Courselevel);
    public List<Course> findByCourseDept (String CourseDept);

}


