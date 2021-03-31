package com.COMP3004.CMS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.util.BsonUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.data.annotation.Id;
import java.util.*;

@Component
public class User extends UserCreateFactory{
    @Id
    protected String username;
    protected String password;
    protected String role;
    protected int id;
    protected String firstname;
    protected String lastname;
    protected boolean active;
    protected String birthdate;

   List<String> roles = Arrays.asList("Admin", "Professor", "Student");

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }

    public boolean getActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

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
        private ArrayList<Course> courseList;

        public String getBirthdate() {
            return birthdate;
        }

        public void setBirthdate(String birthdate) {
            this.birthdate = birthdate;
        }

        public Student(String username, String password, String role, int id, String birthdate, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            setBirthdate(birthdate);
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
//            ArrayList<Course> retrieved = new ArrayList<Course>();
//            System.out.println("retrieving courses from Mongo");

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