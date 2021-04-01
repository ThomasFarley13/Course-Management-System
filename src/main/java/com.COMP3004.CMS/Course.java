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
    @Getter @Setter protected ArrayList<Deliverable> deliverables; //Need to change as array of string IDs

    @Id
    @Getter @Setter protected String courseCode;
    @Getter @Setter  protected int courselevel;
    @Getter @Setter protected int coursenumber;
    @Getter @Setter protected String courseDept;
    @Getter @Setter protected User.Professor professor;
    @Getter @Setter  protected ArrayList<Student> students;



    //Deliverable methods -- NEED TO BE READJUSTED AS SEPHER'S METHOD
    public void createDeliverable(String assignmentName, String description, int daysUntilDue, int weighting){
        deliverables.add(new Deliverable(assignmentName,description,daysUntilDue,weighting));
    }

    public void editDeliverable(String assignmentName, String description, int daysUntilDue, int weighting){
        int targetIndex = 0;

        for (int index = 0; index < deliverables.size()-1; index++){
            if (deliverables.get(index).assignmentName.equals(assignmentName)){
                targetIndex = index;
            }
        }

        deliverables.get(targetIndex).setDescription(description);
        deliverables.get(targetIndex).setDueDate(daysUntilDue);
        deliverables.get(targetIndex).setWeighting(weighting);
    }

    public void deleteDeliverable(String targetName){
        for (int index = 0; index < deliverables.size()-1; index++){
            if (deliverables.get(index).assignmentName.equals(targetName)){
                deliverables.remove(index);
                break;
            }
        }
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

    public Course() {
        this.courseName = null;
        this.courseCode = null;
        this.courselevel = 0;
        this.coursenumber = 0;
        this.courseDept = null;
    }

    public Course(String courseName, String courseCode) {
        this.courseName = courseName;
        this.courseCode = courseCode;
    }


    public Course(String courseName, String courseCode, int courselevel, int coursenumber, String courseDept) {
        this.courseName = courseName;
        this.courseCode = courseCode;
        this.courselevel = courselevel;
        this.coursenumber = coursenumber;
        this.courseDept = courseDept;
    }

}


