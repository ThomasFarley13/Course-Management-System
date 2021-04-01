package com.COMP3004.CMS;

import com.COMP3004.CMS.User.*;
import org.springframework.data.annotation.Id;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;

public class Course {
    protected String courseName;
    protected ArrayList<Deliverable> deliverables;

    @Id
    protected String courseCode;
    protected int courselevel;
    protected int coursenumber;
    protected String courseDept;
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

    //Deliverable methods
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

     class Deliverable {
        protected String assignmentName;
        private String description;
        final Calendar dueDate = Calendar.getInstance();
        private int weighting;

        //public String getAssignmentName() { return assignmentName; } Assignment name should not be changeable imo
        public String getDescription() { return description; }
        public Date getDueDate() { return dueDate.getTime(); }
        public int getWeighting() { return weighting; }

        public void setAssignmentName(String newName) { this.assignmentName = newName; }
        public void setDescription (String newDesc) { this.description = newDesc; }
        public void setDueDate (int daysFromNow) { dueDate.add(Calendar.DATE, daysFromNow); }
        public void setWeighting (int newWeight) { this.weighting = newWeight; }

        public Deliverable(String assignmentName, String description, int daysUntilDue, int weighting){
            this.assignmentName = assignmentName;
            this.description = description;
            dueDate.add(Calendar.DATE, daysUntilDue);
            this.weighting = weighting;
        }
    }
