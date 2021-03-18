package com.COMP3004.CMS;

import com.COMP3004.CMS.User.*;
import java.util.ArrayList;

public class Course {
    protected String courseName;
    protected String courseCode;
    protected User.Professor professor;
    protected ArrayList<Student> students;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public ArrayList<Student> retrieveStudents() {
        ArrayList<Student> retrieved = new ArrayList<Student>();
        System.out.println("retrieving students from Mongo");

        return retrieved;
    }

    @Override
    public String toString() {
        return String.format(
                "User[Course='%s', Course Code='%s']",
                courseName, courseCode);
    }

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }
}
