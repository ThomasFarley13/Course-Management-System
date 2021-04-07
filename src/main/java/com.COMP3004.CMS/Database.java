package com.COMP3004.CMS;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;



interface UserDatabase extends MongoRepository<User,String> {

    public User findByUsername(String username);
    public User deleteByUsername(String username);
    public User findByUsernameAndRole(String username, String role);
    public User findByUsernameAndPassword(String username, String password);
    public User findTopByOrderByIdDesc();
    public List<User> findByActiveIsFalse();
    public List<User> findAll();
    public List<User> findByRole(String role);
    public List<User> findByGender(String gender);
    public List<User> findByBirthdateIsNotNull();
}


interface CourseDatabase extends MongoRepository<Course,String> {
    public Course findByCourseCode(String courseCode);
    public List<Course> findBycoursenumber (int coursenum);
    public List<Course> findByCoursenumberAndCourseDept (int coursenum,String courseDept);
    public List<Course> findByCourselevelAndCourseDept (int courselevel,String courseDept);
    public List<Course> findByCoursenumberAndCourselevelAndCourseDept (int coursenum, int courselevel,String courseDept);
    public List<Course> findByCourselevel (int Courselevel);
    public List<Course> findByCourseDept (String CourseDept);
    public List<Course> findAll();

}


interface DeliverableDatabase extends MongoRepository<Deliverable,String> {

    //public Deliverable findDeliverableByDeliverableID (String DeliverableID);

}

