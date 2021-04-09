package com.COMP3004.CMS;

import com.COMP3004.CMS.User.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;
@Getter @Setter
public class Course {



    @Getter @Setter protected String courseName;

    @Getter @Setter protected ArrayList<String> deliverables;


    @Id
    @Getter @Setter protected String courseCode;
    @Getter @Setter protected int courselevel;
    @Getter @Setter protected int coursenumber;
    @Getter @Setter protected String courseDept;
    @Getter @Setter protected String courseInfo;
    @Getter @Setter protected int capacity;
    @Getter @Setter protected String professor;
    @Getter @Setter protected ArrayList<String> students;
    @Getter @Setter protected String registerByDate;
    @Getter @Setter protected String withdrawByDate;



    public ArrayList<Student> retrieveStudents() {
        ArrayList<Student> retrieved = new ArrayList<Student>();
        System.out.println("retrieving students from Mongo");

        return retrieved;
    }

    @Override
    public String toString() {
        return String.format(
                "User[Course='%s', Course Code='%s', Students = '%s']",
                courseName, courseCode, students);
    }

    public Course() {
        this.courseName = null;
        this.courseCode = null;
        this.courselevel = 0;
        this.coursenumber = 0;
        this.courseDept = null;
        students = new ArrayList<String>();
        deliverables = new ArrayList<String>();
    }

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        students = new ArrayList<String>();
        deliverables = new ArrayList<String>();
    }


    public Course(String courseName, String courseCode, int courselevel, int coursenumber, String courseDept) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courselevel = courselevel;
        this.coursenumber = coursenumber;
        this.courseDept = courseDept;
        this.courseInfo = "";
        capacity =80; // default capacity
        students = new ArrayList<String>();
        this.registerByDate = "2022-08-31";
        this.withdrawByDate = "2022-08-31";
        deliverables = new ArrayList<String>();
    }
    public Course(String courseName, String courseCode, int courselevel, int coursenumber, String courseDept, int capacity) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courselevel = courselevel;
        this.coursenumber = coursenumber;
        this.courseDept = courseDept;
        this.courseInfo = "";
        this.capacity = capacity;
        students = new ArrayList<String>();
        this.registerByDate = "2022-08-31";
        this.withdrawByDate = "2022-08-31";
        deliverables = new ArrayList<String>();
    }

    public void addstudent(String StuUName) { students.add(StuUName); }

    public void addDeliverable(String deliverableID){this.deliverables.add(deliverableID);}

    public String getDeliverable(String deliverableId){
        for (int x = 0; x < deliverables.size(); x++){
            if (deliverables.get(x).equals(deliverableId)){
                return deliverables.get(x);
            }
        }
        return null;
    }

    public void removestudent(String StuUName) {
        students.remove(StuUName);
    }

    public void assignProf (String profUName){
        professor = profUName;
    }

    public void deassignProf (String profUName){
        professor = null;
    }

}



