package com.COMP3004.CMS;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.annotation.Id;
import java.util.*;

@Component
@Getter @Setter

public class User extends UserCreateFactory{

    @Id
    @Getter @Setter protected String username;
    @Getter @Setter  protected String password;
    @Getter @Setter protected String role;
    @Getter @Setter protected int id;
    @Getter @Setter protected String firstname;
    @Getter @Setter protected String lastname;
    @Setter protected boolean active;

    public boolean getActive() { return active; }

   List<String> roles = Arrays.asList("Admin", "Professor", "Student");


    protected void createUsername(String newUsername ) {
        System.out.println("Old username is <" + this.username
                + ">\nNew username is <" + newUsername + ">");

        setUsername(newUsername);
    }

//    protected abstract void createPermissions();

    public User() {
        this.username = null;
        this.password = null;
        this.role = null;
        this.id = 0;
        this.active = false;
    }

    public User(String username, String password, String role, int id, String firstname, String lastname) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
        this.active = false;
        this.firstname = firstname;
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return String.format(
                "User[username='%s', password='%s', role= '%s', id = '%s', active = '%s']",
                username, password, role, id, active);
    }

    @Override
    public User createUser(String username, String password, String role, int id, String birthdate, String firstname, String lastname){
        switch (role) {
            case ("Student"):
                return new Student(username, password, role, id, birthdate, firstname, lastname);
            case ("Professor"):
                return new Professor(username, password, role, id, firstname, lastname);
            case ("Admin"):
                return new Admin(username, password, role, id, "Admin", "User");
            default:
                throw new IllegalStateException("Unexpected value: " + role);
        }
    }

    public class Student extends User{

        @Getter @Setter protected String birthdate;
        private ArrayList<Course> courseList;


        public Student(String username, String password, String role, int id, String birthdate, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            courseList = new ArrayList<String>();
            setBirthdate(birthdate);
        }

        public ArrayList<String> retrieveCourses() {
                ArrayList<String> retrieved = new ArrayList<String>();
                System.out.println("retrieving courses from Mongo");

                return retrieved;
        }

        public Course createRegistrationRequest(){
            return new Course(null, null);
        }
    }

    public class Professor extends User{
        private ArrayList<String> assignedCourses;


        public ArrayList<Course> retrieveCourses() {
           ArrayList<Course> retrieved = new ArrayList<Course>();
           System.out.println("retrieving courses from Mongo");
          
           return assignedCourses;
        }

        public void assignCourse(Course course){ assignedCourses.add(course); }

        public String createDeliverable(){
            return "<link to deliverable here>";
        }

        public void submitGradesDeliverable(String deliverableID, int grade, Student student) {};

        public void submitFinalGrade(int grade, Student student) {};

        public Professor(String username, String password, String role, int id, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            assignedCourses = new ArrayList<String>();
        }
    }

    public class Admin extends User{
        public Admin(String username, String password, String role, int id, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            setActive(true);
        }

        public Course createCourse(String courseName, String courseCode) {
            return new Course(courseName, courseCode);
        }

        public void delCourse(Course course) {}

        //return true on successful registration
        public boolean processRegistration(User user, Course course) {
            return false;
        }

        //return true on successful withdrawal
        public boolean processWithdrawal(User user, Course course) {
            return false;
        }
    }
}