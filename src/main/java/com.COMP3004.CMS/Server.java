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

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserCreateFactory factory = new User();

        repository.deleteAll();

        User admin = factory.createUser("Admin", "Password123", "Admin", 1,"null", "Admin", "User");
        User abdul = factory.createUser("Abdul", "Password223", "Student", 2, "2000-07-30", "Abdul", "Kazal");
        User anita = factory.createUser("Anita", "Password323", "Student", 3, "2000-03-20","Anita", "Chau");
        User sepehr = factory.createUser("Sepehr", "Password423","Student", 4, "1994-12-25","Sepehr", "Eslami Amirabadi");
        User thomas = factory.createUser("Thomas", "Password523","Student", 5,"2000-09-30", "Thomas", "Farley");
        User professor1 = factory.createUser("Professor1", "Password623","Professor", 6,"null", "Big", "Sean");
        // user test data

        abdul.setActive(true);
        thomas.setActive(true);
        professor1.setActive(true);


        repository.save(admin);
        repository.save(abdul);
        repository.save(anita);
        repository.save(sepehr);
        repository.save(thomas);
        repository.save(professor1);



        //course test Data

        Courserepository.save(new Course("Object Oriented Software Programming","3004B",3000,3004,"Computer Science"));
        Courserepository.save(new Course("The origin of Planets","2419B",2000,2419,"Earth Science"));
        Courserepository.save(new Course("Pro Basket Weaving", "1002D", 1000, 1002, "Liberal Arts"));

//        Assigning course(s) to test prof

        String course1 = "3004B";
        System.out.println(Courserepository.findCourseByCourseCode(course1));


//        // fetch all users
        System.out.println("Users found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }
        System.out.println();
//
//        System.out.println("Professor1 Courses: ");
//        for(int index = 0; index > ((User.Professor) professor1).retrieveCourses().size()-1; index++){
//            System.out.println(((User.Professor) professor1).retrieveCourses().get(index).getCourseCode());
//        }
    }
}








