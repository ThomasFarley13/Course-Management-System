package com.COMP3004.CMS;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;

public class Deliverable {

    protected String details;
    protected int weighting;
    protected String name;
    protected Calendar dueDate = Calendar.getInstance();
    @Id
    protected String deliverableID;
    protected String courseCode;
    protected String owner;


    HashMap<String, HashMap> submissions = new HashMap<String, HashMap>(); // this will be of form ('username'=> hashMap)
                                                                            //Where the inner hashmap contains: ('submissionLink'=> 'link', 'grade'=> 'number', 'overdue'=>'boolean')


    //Default Constructor
    public Deliverable () {
        this.details = null;
        this.weighting = 0;
        this.deliverableID = null;
        this.courseCode = null;
        this.name = null;
        this.owner = null;
        HashMap<String, HashMap> submissions = new HashMap<String, HashMap>();
    }

    public void setDetails(String details) {this.details = details;}
    public void setWeighting(int weighting) {this.weighting = weighting;}
    public void setDueDate(int daysDue) {dueDate.add(Calendar.DATE, daysDue);}
    public void setName(String name) {this.name = name;}
    public void setOwner(String professor){this.owner = professor;}

    public void addNewSubmission(String userName, String subLink){
        System.out.println("Submitee Username: " + userName);
        System.out.println("Submitee Link: " + subLink);

        //Checking to see previously existing submission to overwrite
        submissions.remove(userName); //Does nothing if there is no such submission


        //Creating the inner hash data
        HashMap<String,String> innerHash = new HashMap<String, String>();
        innerHash.put("submissionLink", subLink);
        innerHash.put("grade", "0");
        innerHash.put("overdue", "false");

        //Finding out if submission is overdue
        Calendar currentTime = Calendar.getInstance();
        if (dueDate.before(currentTime)){
            innerHash.remove("overdue");
            innerHash.put("overdue", "true");
        }

        //Adding to the main hash
        submissions.put(userName, innerHash);

    }

    public Deliverable(String courseID, String deliverableID) {
        this.courseCode = courseID;
        this.deliverableID = deliverableID;
        this.details = null;
        this.weighting = 0;
        this.name = null;
        this.owner = null;
        HashMap<String, HashMap> submissions = new HashMap<String, HashMap>();
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

