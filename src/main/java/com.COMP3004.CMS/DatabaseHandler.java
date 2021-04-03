package com.COMP3004.CMS;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.stereotype.Service;

@Service
public class DatabaseHandler  {
    ArrayList<observer> Observers;

    @Autowired
    studentDetails stu;

    @Autowired
    profDetails prof;

    @Autowired
    courseDetails cou;

    public DatabaseHandler() {
        ArrayList<observer> Observers = new ArrayList<observer>();
        /*Observers.add(stu);
        Observers.add(prof);
        Observers.add(c);*/
    }

    public void attach(observer o) {
        Observers.add(o);
    }

    public void detach(observer o) {
        Observers.remove(o);
    }

    public void notifyObservers (String actionTaken, String ObjChanged,String Agent, String CourseID, String Extra) {
        /*for (int i = 0; i<Observers.size();i++){
            Observers.get(i).updateRecords(actionTaken,ObjChanged,Agent,CourseID,Extra );
        }*/
        stu.updateRecords(actionTaken,ObjChanged,Agent,CourseID,Extra );
        prof.updateRecords(actionTaken,ObjChanged,Agent,CourseID,Extra);
        cou.updateRecords(actionTaken,ObjChanged,Agent,CourseID,Extra);
    }

    public boolean register_student(String agent, String CourseID) {
        notifyObservers("Register","Course",agent,CourseID,null);
        return false;
    }

    public boolean deregister_student(String agent, String CourseID) {
        notifyObservers("Deregister","Course",agent,CourseID,null);
        return false;
    }
    public boolean assign_prof(String agent, String CourseID) {
        notifyObservers("Assign","Course",agent,CourseID,null);
        return false;
    }

    public boolean deassign_prof(String agent, String CourseID) {
        notifyObservers("Deassign","Course",agent,CourseID,null);
        return false;
    }

    public boolean add_course(String agent, String CourseID) {
        notifyObservers("Add","Course",agent,CourseID,null);
        return false;
    }

    public boolean delete_course(String agent, String CourseID) {
        notifyObservers("Delete","Course",agent,CourseID,null);
        return false;
    }

    public boolean Add_deliverable(String agent, String CourseID,String DID) {
        notifyObservers("Add","Deliverable",agent,CourseID,DID);
        return false;
    }

    public boolean remove_deliverable (String agent, String CourseID,String DID) {
        notifyObservers("Delete","Deliverable",agent,CourseID,DID);
        return false;
    }

    public boolean submit_deliverable (String agent, String DID,String sumbissionlink) {
        notifyObservers("submit","Deliverable",agent,DID,sumbissionlink);
        return false;
    }


}

@Service
abstract class observer {

    String type;
    @Autowired
    protected UserDatabase Userrepository;

    @Autowired
    protected CourseDatabase Courserepository;

    @Autowired
    protected DeliverableDatabase Deliverablerepository;

    public observer(String type) {this.type = type; }

    public abstract void updateRecords(String action, String ObjChanged, String Agent, String CourseID, String Extra);

}


@Service
class studentDetails extends observer {
    public studentDetails() {
        super("Student");
    }

    @Override
    public void updateRecords(String action, String ObjChanged, String Agent, String CourseID, String Extra) {
        if (action.equals("Register")) {
            User.Student temp = (User.Student) Userrepository.findByUsername(Agent);
            temp.register(CourseID);
            Userrepository.save(temp);
        } else if (action.equals("Deregister")) {
            User.Student temp = (User.Student) Userrepository.findByUsername(Agent);
            temp.deregister(CourseID);
            Userrepository.save(temp);
        } else if (action.equals("Delete") && ObjChanged.equals("Course")) {
            User.Student temp = (User.Student) Userrepository.findByUsername(Agent);
            temp.deregister(CourseID);
            Userrepository.save(temp);
        } else if (action.equals("Grade")){
            User.Student temp = (User.Student) Userrepository.findByUsername(Agent);
            temp.grading(CourseID,Extra);
            Userrepository.save(temp);
        }

    }
}


@Service
class profDetails extends observer {
    public profDetails() {
        super("Professor");
    }


    @Override
    public void updateRecords(String action, String ObjChanged, String Agent, String CourseID, String Extra) {
        if (action.equals("Assign")) {
            User.Professor temp = (User.Professor) Userrepository.findByUsername(Agent);
            temp.assign(CourseID);
            Userrepository.save(temp);
        } else if (action.equals("Deassign")) {
            User.Professor temp = (User.Professor) Userrepository.findByUsername(Agent);
            temp.deassign(CourseID);
            Userrepository.save(temp);
        } else if (action.equals("Add") && ObjChanged.equals("Deliverable")) {
            UUID uuid = UUID.randomUUID();
            Deliverable d = new Deliverable(CourseID, uuid.toString());
            Deliverablerepository.save(d);
        } else if (action.equals("Delete") && ObjChanged.equals("Deliverable")) {
            //Deliverable t = Deliverablerepository.findDeliverableByDeliverableID(Extra);
            //Deliverablerepository.delete(t);
        }
    }
}


@Service
class courseDetails extends observer {
    public courseDetails() {
        super("Course");
    }

    @Override
    public void updateRecords(String action, String ObjChanged, String Agent, String CourseID, String Extra) {
        if (action.equals("Register")) {
            Course c = Courserepository.findByCourseCode(CourseID);
            c.addstudent(Agent);
            Courserepository.save(c);
        } else if (action.equals("Deregister")) {
            Course c = Courserepository.findByCourseCode(CourseID);
            c.removestudent(Agent);
            Courserepository.save(c);
        } else if (action.equals("Add") && ObjChanged.equals("Course")) {
            if (Agent.equals("Admin")) {
                Course temp = new Course("", CourseID);
                Courserepository.save(temp);
                //the other classes will have to populate the data further
            }
        } else if (action.equals("Assign")) {
            Course c = Courserepository.findByCourseCode(CourseID);
            c.assignProf(Agent);
            Courserepository.save(c);
        } else if (action.equals("Deassign")) {
            Course c = Courserepository.findByCourseCode(CourseID);
            c.deassignProf(Agent);
            Courserepository.save(c);
        } else if (action.equals("Delete") && ObjChanged.equals("Course")) {
            Course c = Courserepository.findByCourseCode(CourseID);
            Courserepository.delete(c);
        } else if (action.equals("Add") && ObjChanged.equals("Deliverable")) {

        } else if (action.equals("Delete") && ObjChanged.equals("Deliverable")) {

        }
    }
}
