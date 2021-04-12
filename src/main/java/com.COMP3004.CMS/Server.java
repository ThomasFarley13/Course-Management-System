package com.COMP3004.CMS;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;
import java.util.logging.Handler;

@SpringBootApplication
@ComponentScan("com")
public class Server implements CommandLineRunner {

    @Autowired
    private UserDatabase repository;
    @Autowired
    private CourseDatabase Courserepository;
    @Autowired
    private DeliverableDatabase dRepository;
    @Autowired
    DatabaseHandler handler ;





    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserCreateFactory factory = new User();



        repository.deleteAll();
        Courserepository.deleteAll();
        dRepository.deleteAll();

        User.Admin admin = (User.Admin) factory.createUser("Admin", "Password123", "Admin", 1,"null","null", "Admin", "User");
        User.Student abdul = (User.Student) factory.createUser("Abdul", "Password223", "Student", 2, "2000-07-30","male", "Abdul", "Kazal");
        User.Student anita = (User.Student) factory.createUser("Anita", "Password323", "Student", 3, "2000-03-20","female","Anita", "Chau");
        User.Student sepehr = (User.Student) factory.createUser("Sepehr", "Password423","Student", 4, "1994-12-25","male","Sepehr", "Eslami Amirabadi");
        User.Student thomas = (User.Student) factory.createUser("Thomas", "Password523","Student", 5,"2000-09-30", "other","Thomas", "Farley");
        User.Professor professor1 = (User.Professor) factory.createUser("Professor1", "Password623","Professor", 6,"null","null", "Big", "Sean");

        User.Professor testProfessor = (User.Professor) factory.createUser("TestProfessor", "Password723", "Professor", 7, "null", "null", "Testing", "Tester");
        // user test data

        //user test data


        thomas.grading("3004B","75");
        thomas.grading("3007A","23");
        thomas.grading("2003C","12");
        anita.grading("2003C","0");


        abdul.setActive(true);
        thomas.setActive(true);
        professor1.setActive(true);
        testProfessor.setActive(true);


        repository.save(admin);
        repository.save(abdul);
        repository.save(anita);
        repository.save(sepehr);
        repository.save(thomas);
        repository.save(professor1);
        repository.save(testProfessor);



        //course test Data
        Course tempCourse;

        tempCourse = new Course("Object Oriented Software Programming","3004B",3000,3004,"Computer Science");
        tempCourse.setWithdrawByDate("2020-12-31");
        Courserepository.save(tempCourse);
        handler.assign_prof(professor1.getUsername(), "3004B");
        tempCourse = new Course("The origin of Planets","2419B",2000,2419,"Earth Science");
        tempCourse.setRegisterByDate("2020-12-31");
        Courserepository.save(tempCourse);
        tempCourse = new Course("Pro Basket Weaving", "1002D", 1000, 1002, "Liberal Arts");
        tempCourse.setWDNgradeStartDate("2021-04-10");
        Courserepository.save(tempCourse);
        Courserepository.save(new Course("Programming Paradigms","COMP3007B",3000,3007,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP3007B");
        Courserepository.save(new Course("Applied Cryptography","COMP4109A",4000,4109,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP4109A");
        Courserepository.save(new Course("Fundamentals Web Applications","COMP2406A",2000,2406,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP2406A");
        Courserepository.save(new Course("Operating Systems","COMP3000B",3000,3000,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP3000B");
        Courserepository.save(new Course("Design & Analysis of Algorithms","COMP3804A",3000,3804,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP3804A");
        Courserepository.save(new Course("Intro to Systems Programming","COMP2401A",2000,2401,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP2401A");
        Courserepository.save(new Course("Intro to Software Engineering","COMP2404A",2000,2404,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP2404A");
        Courserepository.save(new Course("Abstract Data Types/Algorithms","COMP2402B",2000,2402,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP2402B");
        Courserepository.save(new Course("Linear Algebra II","MATH2107A",2000,2107,"Math"));
        handler.assign_prof(testProfessor.getUsername(), "MATH2107A");
        Courserepository.save(new Course("Intro to Computer Science II","COMP1406B",1000,1406,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP1406B");
        Courserepository.save(new Course("Intro to Computer Science I","COMP1405B",1000,1405,"Computer Science"));
        handler.assign_prof("Professor1", "COMP1405B");
        Courserepository.save(new Course("Linear Algebra I","MATH1107A",1000,1107,"Math"));
        handler.assign_prof(testProfessor.getUsername(), "MATH1107A");
        Courserepository.save(new Course("Discrete Structures I","COMP1805A",1000,1805,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP1805A");
        Courserepository.save(new Course("Discrete Structures II","COMP2804A",2000,2804,"Computer Science"));
        handler.assign_prof(testProfessor.getUsername(), "COMP2804A");
        Courserepository.save(new Course("Introduction to Ecology","BIOL2600A",2000,2600,"Biology"));
        handler.assign_prof(testProfessor.getUsername(), "BIOL2600A");
        Courserepository.save(new Course("Mysteries of the Mind","CGSC1001B",1000,1001,"Cognitive Science"));
        handler.assign_prof(testProfessor.getUsername(), "CGSC1001B");
        Courserepository.save(new Course("Co-op","COOP1000D",1000,1000,"Co-op"));
        Courserepository.save(new Course("Introduction to Ecology","BIOL2600A",2000,2600,"Biology"));
        handler.assign_prof(testProfessor.getUsername(), "BIOL2600A");
        Courserepository.save(new Course("Evolutionary Concepts","BIOL3609A",3000,3609,"Biology"));
        handler.assign_prof(testProfessor.getUsername(), "BIOL3609A");
        Courserepository.save(new Course("General Biochemistry I","BIOC3101A",3000,3101,"Biochemistry"));
        handler.assign_prof(testProfessor.getUsername(), "BIOC3101A");
        tempCourse = new Course("Computational Systems Biology","COMP4308A",4000,4308,"Computer Science");
        tempCourse.setWithdrawByDate("2020-12-31");
        Courserepository.save(tempCourse);

        //populating withdrawal and registration dates for testing purposes
        Course c = Courserepository.findByCourseCode("BIOL2600A");
        c.setRegisterByDate("2021-04-7");
        c.setWithdrawByDate("2021-04-8");
        Courserepository.save(c);

        c = Courserepository.findByCourseCode("CGSC1001B");
        c.setWithdrawByDate("2021-04-8");
        Courserepository.save(c);

        handler.assign_prof(professor1.getUsername(), "COMP4308A");


        Course temp = Courserepository.findByCourseCode("COMP4308A");
        temp.setCourseInfo("Course info Placeholder");
        Courserepository.save(temp);

        handler.register_student("Abdul","3004B");
        handler.register_student("Thomas","3004B");
        handler.register_student("Abdul","COMP4308A");
        handler.register_student("Abdul","CGSC1001B");

        //Assigning course(s) to test prof
        ((User.Professor) professor1).assignCourse("3004B");
        ((User.Professor) professor1).assignCourse("COMP4308A");

        repository.save(professor1);

        //Creating a couple of deliverables
        handler.Add_deliverable("Professor1", "3004B", "95");
        Deliverable testD = dRepository.findDeliverableByDeliverableID("95");
        testD.setDueDate(10);
        testD.setDetails("What's 1+1 (100 marks): ");
        testD.setName("The Syllabus Test");
        testD.setWeighting(30);
        testD.addNewSubmission("Abdul", "www.google.ca");
        dRepository.save(testD);

        handler.Add_deliverable("Professor1", "COMP4308A", "96");
        Deliverable testD2 = dRepository.findDeliverableByDeliverableID("96");
        testD2.setDueDate(10);
        testD2.setDetails("What's 1+1 (100 marks): ");
        testD2.setName("Difficult Computer Project");
        testD2.setWeighting(30);
        testD2.addNewSubmission("Abdul", "www.bing.ca");
        dRepository.save(testD2);

//        dRepository.findDeliverableByDeliverableID("95").setDueDate(10);
//        dRepository.findDeliverableByDeliverableID("95").setWeighting(30);
//        dRepository.findDeliverableByDeliverableID("95").setDetails("Syllabus test: What's 1+1 (100 marks): ");

        //handler.delete_course("", "3004B");

        System.out.println("Server now up..");
        //System.out.println(dRepository.findDeliverableByDeliverableID("95").details);


//        // fetch all users

        //stu.updateRecords("Register","Course","Abdul","2419B",null);
        // fetch all users
//        System.out.println("Users found with findAll():");
//        System.out.println("-------------------------------");
//        for (User user : repository.findAll()) {
//            System.out.println(user);
//
//        }
//        System.out.println();
//
//        System.out.println("Courses found with findAll():");
//        System.out.println("-------------------------------");
//        for (Course course : Courserepository.findAll()) {
//            System.out.println(course);
//        }
//        System.out.println();
//
//        System.out.println("Professor1 Courses: ");
//        for(int index = 0; index > ((User.Professor) professor1).retrieveCourses().size()-1; index++){
//            System.out.println(((User.Professor) professor1).retrieveCourses().get(index).getCourseCode());
//        }
    }
}








