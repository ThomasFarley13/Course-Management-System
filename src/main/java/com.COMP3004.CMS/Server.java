package com.COMP3004.CMS;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootApplication
@ComponentScan("com")
public class Server implements CommandLineRunner {

    @Autowired
    private UserDatabase repository;
    @Autowired
    private CourseDatabase Courserepository;
    @Autowired
    DatabaseHandler handler ;





    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserCreateFactory factory = new User();



        //repository.deleteAll();
        Courserepository.deleteAll();

        User.Admin admin = (User.Admin) factory.createUser("Admin", "Password123", "Admin", 1,"null","null", "Admin", "User");
        User.Student abdul = (User.Student) factory.createUser("Abdul", "Password223", "Student", 2, "2000-07-30","male", "Abdul", "Kazal");
        User.Student anita = (User.Student) factory.createUser("Anita", "Password323", "Student", 3, "2000-03-20","female","Anita", "Chau");
        User.Student sepehr = (User.Student) factory.createUser("Sepehr", "Password423","Student", 4, "1994-12-25","male","Sepehr", "Eslami Amirabadi");
        User.Student thomas = (User.Student) factory.createUser("Thomas", "Password523","Student", 5,"2000-09-30", "other","Thomas", "Farley");
        User.Professor professor1 = (User.Professor) factory.createUser("Professor1", "Password623","Professor", 6,"null","null", "Big", "Sean");
        // user test data

        thomas.grading("3004B","B");
        thomas.grading("3007A","A-");
        thomas.grading("2003C","D-");
        anita.grading("2003C","B+");


        abdul.setActive(true);
        thomas.setActive(true);
        professor1.setActive(true);


        repository.save(admin);
        repository.save(abdul);
        repository.save(anita);
        repository.save(sepehr);
        repository.save(thomas);



        //course test Data

        Courserepository.save(new Course("Object Oriented Software Programming","3004B",3000,3004,"Computer Science"));
        Courserepository.save(new Course("The origin of Planets","2419B",2000,2419,"Earth Science"));
        Courserepository.save(new Course("Pro Basket Weaving", "1002D", 1000, 1002, "Liberal Arts"));
        Courserepository.save(new Course("Programming Paradigms","COMP3007B",3000,3007,"Computer Science"));
        Courserepository.save(new Course("Applied Cryptography","COMP4109A",4000,4109,"Computer Science"));
        Courserepository.save(new Course("Fundamentals Web Applications","COMP2406A",2000,2406,"Computer Science"));
        Courserepository.save(new Course("Operating Systems","COMP3000B",3000,3000,"Computer Science"));
        Courserepository.save(new Course("Design & Analysis of Algorithms","COMP3804A",3000,3804,"Computer Science"));
        Courserepository.save(new Course("Intro to Systems Programming","COMP2401A",2000,2401,"Computer Science"));
        Courserepository.save(new Course("Intro to Software Engineering","COMP2404A",2000,2404,"Computer Science"));
        Courserepository.save(new Course("Abstract Data Types/Algorithms","COMP2402B",2000,2402,"Computer Science"));
        Courserepository.save(new Course("Linear Algebra II","MATH2107A",2000,2107,"Math"));
        Courserepository.save(new Course("Intro to Computer Science II","COMP1406B",1000,1406,"Computer Science"));
        Courserepository.save(new Course("Intro to Computer Science I","COMP1405B",1000,1405,"Computer Science"));
        Courserepository.save(new Course("Linear Algebra I","MATH1107A",1000,1107,"Math"));
        Courserepository.save(new Course("Discrete Structures I","COMP1805A",1000,1805,"Computer Science"));
        Courserepository.save(new Course("Discrete Structures II","COMP2804A",2000,2804,"Computer Science"));
        Courserepository.save(new Course("Introduction to Ecology","BIOL2600A",2000,2600,"Biology"));
        Courserepository.save(new Course("Mysteries of the Mind","CGSC1001B",1000,1001,"Cognitive Science"));
        Courserepository.save(new Course("Co-op","COOP1000D",1000,1000,"Co-op"));
        Courserepository.save(new Course("Introduction to Ecology","BIOL2600A",2000,2600,"Biology"));
        Courserepository.save(new Course("Evolutionary Concepts","BIOL3609A",3000,3609,"Biology"));
        Courserepository.save(new Course("General Biochemistry I","BIOC3101A",3000,3101,"Biochemistry"));
        Courserepository.save(new Course("Computational Systems Biology","COMP4308A",4000,4308,"Computer Science"));

        ((User.Professor) professor1).assignCourse("3004B");
        ((User.Professor) professor1).assignCourse("1405B");

        repository.save(professor1);

        System.out.println("Server now up..");
//        Assigning course(s) to test prof




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








