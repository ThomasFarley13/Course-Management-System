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
    private Database repository;

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        UserCreateFactory factory = new User();

        //repository.deleteAll();

        User admin = factory.createUser("Admin", "Password123", "Admin", 1,"null", "Admin", "User");
        User abdul = factory.createUser("Abdul", "Password223", "Student", 2, "2000-07-30", "Abdul", "Kazal");
        User anita = factory.createUser("Anita", "Password323", "Student", 3, "2000-03-20","Anita", "Chau");
        User sepehr = factory.createUser("Sepehr", "Password423","Student", 4, "1994-12-25","Sepehr", "Eslami Amirabadi");
        User thomas = factory.createUser("Thomas", "Password523","Student", 5,"2000-09-30", "Thomas", "Farley");
        User professor1 = factory.createUser("Professor1", "Password623","Professor", 6,"null", "Big", "Sean");
        // user test data
        repository.save(admin);
        repository.save(abdul);
        repository.save(anita);
        repository.save(sepehr);
        repository.save(thomas);
        repository.save(professor1);
        // fetch all users
        System.out.println("Users found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }
        System.out.println();
    }
}








