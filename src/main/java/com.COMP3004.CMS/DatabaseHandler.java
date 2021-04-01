package com.COMP3004.CMS;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.MongoRepository;

@ComponentScan("com")
public class DatabaseHandler  {

    @Autowired
    private UserDatabase Userrepository;

    @Autowired
    private CourseDatabase Courserepository;

    ArrayList<observer> Observers;

    public DatabaseHandler() {
        ArrayList<observer> Observers = new ArrayList<observer>();
    }
    public void attach(observer o) {
        Observers.add(o);
    }

    public void detach(observer o) {
        Observers.remove(o);
    }

    public void notifyObservers (String actionTaken, String ObjChanged,String Agent, String CourseID) {
        for (int i = 0; i<Observers.size();i++){
            Observers.get(i).updateRecords(actionTaken,ObjChanged,Agent,CourseID);
        }
    }

    public boolean register_student(String agent, String CourseID) {
        notifyObservers("Register","Course",agent,CourseID);
        return false;
    }

    public boolean deregister_student(String agent, String CourseID) {
        notifyObservers("Deregister","Course",agent,CourseID);
        return false;
    }
    public boolean assign_prof(String agent, String CourseID) {
        notifyObservers("Assign","Course",agent,CourseID);
        return false;
    }

    public boolean deassign_prof(String agent, String CourseID) {
        notifyObservers("Deassign","Course",agent,CourseID);
        return false;
    }

    public boolean add_course(String agent, String CourseID) {
        notifyObservers("Add","Course",agent,CourseID);
        return false;
    }

    public boolean delete_course(String agent, String CourseID) {
        notifyObservers("Delete","Course",agent,CourseID);
        return false;
    }

    public boolean Add_deliverable(String agent, String CourseID) {
        notifyObservers("Add","Deliverable",agent,CourseID);
        return false;
    }

    public boolean remove_deliverable (String agent, String CourseID) {
        notifyObservers("Delete","Deliverable",agent,CourseID);
        return false;
    }

    public boolean submit_deliverable (String agent, String CourseID) {
        notifyObservers("submit","Deliverable",agent,CourseID);
        return false;
    }


}

abstract class observer {

    String type;

    public observer (String type) {
        this.type =type;
    }

    public abstract void updateRecords(String action, String ObjChanged, String Agent, String CourseID);


    class studentDetails extends observer {
        public studentDetails () {
            super("Student");

        }

        @Override
        public void updateRecords(String action, String ObjChanged, String Agent, String CourseID) {
            if(action.equals("Register")) {

            }
            else if (action.equals("Deregister")) {

            }
            else if (action.equals("Delete") && ObjChanged.equals("Course")) {

            }
            else if (action.equals("Delete") && ObjChanged.equals("Deliverable")) {

            }
        }
    }

    class profDetails extends observer {
        public profDetails () {
            super("Professor");
        }


        @Override
        public void updateRecords(String action, String ObjChanged, String Agent, String CourseID) {
            if(action.equals("Assign")) {

            }
            else if (action.equals("Deassign")) {

            }
            else if (action.equals("Add") && ObjChanged.equals("Deliverable")) {

            }
            else if (action.equals("Delete") && ObjChanged.equals("Deliverable")) {

            }
        }
    }

    class courseDetails extends observer {
        public courseDetails (){
            super("Course");
        }

        @Override
        public void updateRecords(String action, String ObjChanged, String Agent, String CourseID) {
            if(action.equals("Register")) {

            }
            else if (action.equals("Deregister")) {

            }
            else if (action.equals("Submit")) {

            }
            else if (action.equals("Grade")) {

            }
            else if (action.equals("Add") && ObjChanged.equals("Course")) {

            }
            else if (action.equals("Delete") && ObjChanged.equals("Course")) {

            }
            else if (action.equals("Add") && ObjChanged.equals("Deliverable")) {

            }
            else if (action.equals("Delete") && ObjChanged.equals("Deliverable")) {

            }
        }
    }

}
