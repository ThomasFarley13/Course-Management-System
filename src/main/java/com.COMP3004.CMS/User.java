package com.COMP3004.CMS;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
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
    @Getter @Setter protected String birthdate;
    @Getter @Setter protected String gender;
    @Getter protected ArrayList<String> courseList;
    @Getter protected Hashtable<String, String> grades;
    @Getter ArrayList<String> prevCourses;

    public boolean getActive() {
        return active;
    }


    //unused create username for further expansion
    protected void createUsername(String newUsername) {
        System.out.println("Old username is <" + this.username
                + ">\nNew username is <" + newUsername + ">");

        setUsername(newUsername);
    }

    //default constructor for user
    public User() {
        this.username = null;
        this.password = null;
        this.role = null;
        this.id = 0;
        this.active = false;
    }

    //constructor to help with testing
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
                "User[username='%s', password='%s', role= '%s', id = '%s', active = '%s', birthdate = '%s', grades = '%s']",
                username, password, role, id, active, birthdate, grades);
    }

    @Override
    public User createUser(String username, String password, String role, int id, String birthdate, String gender, String firstname, String lastname) {
        switch (role) {
            case ("Student"):
                return new Student(username, password, role, id, birthdate, gender, firstname, lastname);
            case ("Professor"):
                return new Professor(username, password, role, id, firstname, lastname);
            case ("Admin"):
                return new Admin(username, password, role, id, "Admin", "User");
            default:
                throw new IllegalStateException("Unexpected value: " + role);
        }
    }

    //student class
    public class Student extends User {

        public Student(String username, String password, String role, int id, String birthdate, String gender, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            courseList = new ArrayList<String>();
            prevCourses = new ArrayList<String>();
            grades = new Hashtable<String, String>();
            setBirthdate(birthdate);
            setGender(gender);
        }

        //getter for courses
        public ArrayList<String> retrieveCourses() {
            ArrayList<String> retrieved = new ArrayList<String>();
            System.out.println("retrieving courses from Mongo");

            return courseList;
        }

        //adds a course to the previously taken list
        public void addPrevCourse(String c) {
            prevCourses.add(c);
        }

        //register function
        public void register(String CourseID) {
            courseList.add(CourseID);
            System.out.println("Added the following course: " + CourseID);
        }

        //deregister function
        public void deregister(String CourseID) {
            grading(CourseID, "WDN");
            addPrevCourse(CourseID);
            courseList.remove(CourseID);
        }

        public void removeCourse(String CourseID) {
            courseList.remove(CourseID);
        }

        //grades the course and adds it to hashmap
        public void grading(String CourseID, String grade) {
            grades.put(CourseID, grade);
        }


    }

    //professor class
    public class Professor extends User {


        public ArrayList<String> retrieveCourses() {
            return courseList;
        }

        public void assignCourse(String course) {
            courseList.add(course);
        }

        public void deregisterCourse(String CourseID) {
            courseList.remove(CourseID);
        }

        public void assign(String courseID) {
            courseList.add(courseID);
        }

        public void deassign(String courseID) {
            courseList.remove(courseID);
        }

        public Professor(String username, String password, String role, int id, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            courseList = new ArrayList<>();
        }
    }

    //admin class
    public class Admin extends User {
        public Admin(String username, String password, String role, int id, String firstname, String lastname) {
            super(username, password, role, id, firstname, lastname);
            setActive(true);
        }
    }
}