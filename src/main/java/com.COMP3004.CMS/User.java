package com.COMP3004.CMS;

import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.annotation.Id;
import java.util.*;

public class User{
    @Id
    protected String username;
    protected String password;
    protected String role;
    protected int id;
    protected String firstname;
    protected String lastname;

   List<String> roles = Arrays.asList("Admin", "Professor", "Student");

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }

    public void setRole(String role) { this.role = role; }

    public String getFirstname() { return firstname; }

    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }

    public void setLastname(String lastname) { this.lastname = lastname; }

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
    }

    public User(String username, String password, String role, int id) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format(
                "User[username='%s', password='%s']",
                username, password);
    }

    public class Student extends User{
        private String birthdate;
        private ArrayList<Course> courseList;

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public Student(String username, String password, String role, int id) {
            super(username, password, role, id);
        }

        public ArrayList<Course> retrieveCourses() {
                ArrayList<Course> retrieved = new ArrayList<Course>();
                System.out.println("retrieving courses from Mongo");

                return retrieved;
        }

        public Course createRegistrationRequest(){
            return new Course(null, null);
        }
    }

    public class Professor extends User{
        private ArrayList<Course> assignedCourses;

        public ArrayList<Course> retrieveCourses() {
            ArrayList<Course> retrieved = new ArrayList<Course>();
            System.out.println("retrieving courses from Mongo");

            return retrieved;
        }

        public String createDeliverable(){
            return "<link to deliverable here>";
        }

        public void submitGradesDeliverable(String deliverableID, int grade, Student student) {};
        public void submitFinalGrade(int grade, Student student) {};

        public Professor(String username, String password, String role, int id) {
            super(username, password, role, id);
        }
    }

    public class Admin extends User{
        public Admin(String username, String password, String role, int id) {
            super(username, password, role, id);
        }

        public Course createCourse(String courseName, String courseCode) {
            return new Course(courseName, courseCode);
        }

        public void delCourse(Course course) {}

        public User createUser(String username, String password, String role, int id){
            switch (role) {
                case ("Student"):
                    return new Student(username, password, role, id);
                case ("Professor"):
                    return new Professor(username, password, role, id);
                case ("Admin"):
                    return new Admin(username, password, role, id);
                default:
                    throw new IllegalStateException("Unexpected value: " + role);
            }
        }

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