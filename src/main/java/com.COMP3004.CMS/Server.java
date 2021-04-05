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
        repository.save(professor1);



        //course test Data

        Courserepository.save(new Course("Object Oriented Software Programming","3004B",3000,3004,"Computer Science"));
        Courserepository.save(new Course("The origin of Planets","2419B",2000,2419,"Earth Science"));
        Courserepository.save(new Course("Pro Basket Weaving", "1002D", 1000, 1002, "Liberal Arts"));

//        Assigning course(s) to test prof




//        // fetch all users

        //stu.updateRecords("Register","Course","Abdul","2419B",null);
        // fetch all users
        System.out.println("Users found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);

        }
        System.out.println();

        System.out.println("Courses found with findAll():");
        System.out.println("-------------------------------");
        for (Course course : Courserepository.findAll()) {
            System.out.println(course);
        }
        System.out.println();
//
//        System.out.println("Professor1 Courses: ");
//        for(int index = 0; index > ((User.Professor) professor1).retrieveCourses().size()-1; index++){
//            System.out.println(((User.Professor) professor1).retrieveCourses().get(index).getCourseCode());
//        }
    }
}








