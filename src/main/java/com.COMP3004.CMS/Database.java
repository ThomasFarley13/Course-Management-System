package com.COMP3004.CMS;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


interface UserDatabase extends MongoRepository<User,String> {

    User findByUsername(String username);
    User findByid(int id);
    User deleteByUsername(String username);
    User findByUsernameAndRole(String username, String role);
    User findByUsernameAndPassword(String username, String password);
    User findTopByOrderByIdDesc();
    List<User> findByActiveIsFalse();
    List<User> findAll();
    List<User> findByRole(String role);
    List<User> findByGender(String gender);
    List<User> findByBirthdateIsNotNull();
}


interface CourseDatabase extends MongoRepository<Course,String> {
    Course findByCourseCode(String courseCode);
    List<Course> findBycoursenumber (int coursenum);
    List<Course> findByCoursenumberAndCourseDept (int coursenum,String courseDept);
    List<Course> findByCourselevelAndCourseDept (int courselevel,String courseDept);
    List<Course> findByCourselevel (int Courselevel);
    List<Course> findByCourseDept (String CourseDept);
    List<Course> findByProfessor(String name);
    List<Course> findAll();
}


interface DeliverableDatabase extends MongoRepository<Deliverable,String> {
    Deliverable findDeliverableByDeliverableID (String deliverableID);
    List<Deliverable> findByCourseCode (String courseCode);
    List<Deliverable> findByowner (String owner);
    Deliverable findByownerAndName(String owner, String Name);
    Deliverable findByCourseCodeAndName(String courseCode, String Name);
}

