/*package com.COMP3004.CMS;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;

public class Deliverable {

    String CourseCode;
    @Id
    String DeliverableID;
    ArrayList<JSONObject> studentsubmissions; // this will be of form {username:{sbmission:submissionlink,grade:grade}}


    public Deliverable () {

    }

    public Deliverable (String CourseCode,String DeliverableID) {
        this.DeliverableID = DeliverableID;
        this.CourseCode = CourseCode;
        ArrayList<JSONObject> studentsubmissions = new ArrayList<JSONObject>();
    }



}
*/