package com.COMP3004.CMS;


import lombok.Setter;
import org.springframework.data.annotation.Id;


import java.util.Calendar;
import java.util.HashMap;

public class Deliverable {

    @Setter protected String details;
    @Setter protected int weighting;
    @Setter protected String name;
    protected Calendar dueDate = Calendar.getInstance();
    @Id
    protected String deliverableID;
    protected String courseCode;
    @Setter protected String owner;


    HashMap<String, HashMap> submissions = new HashMap<String, HashMap>(); // this will be of form ('username'=> hashMap)
    //Where the inner hashmap contains: ('submissionLink'=> 'link', 'grade'=> 'number', 'overdue'=>'boolean')


    //Default Constructor
    public Deliverable() {
        this.details = null;
        this.weighting = 0;
        this.deliverableID = null;
        this.courseCode = null;
        this.name = null;
        this.owner = null;
        HashMap<String, HashMap> submissions = new HashMap<String, HashMap>();
    }

    /*
    public void setDetails(String details) {this.details = details;}
    public void setWeighting(int weighting) {this.weighting = weighting;}
    public void setName(String name) {this.name = name;}
    public void setOwner(String professor){this.owner = professor;}
    */
    public void setDueDate(int daysDue) {
        dueDate.add(Calendar.DATE, daysDue);
    }


    public void addNewSubmission(String userName, String subLink) {
        System.out.println("Submitee Username: " + userName);
        System.out.println("Submitee Link: " + subLink);

        //Checking to see previously existing submission to overwrite
        submissions.remove(userName); //Does nothing if there is no such submission


        //Creating the inner hash data
        HashMap<String, String> innerHash = new HashMap<String, String>();
        innerHash.put("submissionLink", subLink);
        innerHash.put("grade", "0");
        innerHash.put("overdue", "false");

        //Finding out if submission is overdue
        Calendar currentTime = Calendar.getInstance();
        if (dueDate.before(currentTime)) {
            innerHash.remove("overdue");
            innerHash.put("overdue", "true");
        }

        //Adding to the main hash
        submissions.put(userName, innerHash);

    }

    public void updateSubmission(String userName, String subLink, String grade) {
        System.out.println("Submitee Username: " + userName);
        System.out.println("Submitee Link: " + subLink);

        //Checking to see previously existing submission to overwrite
        submissions.remove(userName); //Does nothing if there is no such submission


        //Creating the inner hash data
        HashMap<String, String> innerHash = new HashMap<String, String>();
        innerHash.put("submissionLink", subLink);
        innerHash.put("grade", grade);
        innerHash.put("overdue", "false");

        //Finding out if submission is overdue
        Calendar currentTime = Calendar.getInstance();
        if (dueDate.before(currentTime)) {
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


