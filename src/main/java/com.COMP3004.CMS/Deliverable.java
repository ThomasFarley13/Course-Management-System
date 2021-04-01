package com.COMP3004.CMS;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class Deliverable {

    String CourseCode;
    @Id
    String DeliverableID;
    ArrayList<JSONObject> studentsubmissions; // this will be of form {username:{sbmission:submissionlink,grade:grade}}
    String Details;

    public Deliverable () {

    }

    public Deliverable (String CourseCode,String DeliverableID) {
        this.DeliverableID = DeliverableID;
        this.CourseCode = CourseCode;
        ArrayList<JSONObject> studentsubmissions = new ArrayList<JSONObject>();
    }



}


// i commented this to make my git commit easy, you can change it and its dependencies later abdul
/*package com.COMP3004.CMS;
import java.util.Calendar;
import java.util.Date;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.annotation.Id;

public class Deliverable {
    protected String assignmentName;
    private String description;
    final Calendar dueDate = Calendar.getInstance();
    private int weighting;

    @Id
    protected String deliverableId;

    //public String getAssignmentName() { return assignmentName; } Assignment name should not be changeable imo
    public String getDescription() { return description; }
    public Date getDueDate() { return dueDate.getTime(); }
    public int getWeighting() { return weighting; }

    public void setAssignmentName(String newName) { this.assignmentName = newName; }
    public void setDescription (String newDesc) { this.description = newDesc; }
    public void setDueDate (int daysFromNow) { dueDate.add(Calendar.DATE, daysFromNow); }
    public void setWeighting (int newWeight) { this.weighting = newWeight; }

    public Deliverable(){
        this.assignmentName = null;
        this.description = null;
        this.weighting = 0;
    }

    public Deliverable(String assignmentName, String description, int daysUntilDue, int weighting){
        this.assignmentName = assignmentName;
        this.description = description;
        dueDate.add(Calendar.DATE, daysUntilDue);
        this.weighting = weighting;
    }
}*/

