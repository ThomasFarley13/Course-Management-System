package com.COMP3004.CMS;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
public class Server implements CommandLineRunner {

    @Autowired
    private Database repository;

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        repository.deleteAll();

        // user test data
        repository.save(new User("Admin", "Password123", "Admin"));
        repository.save(new User("Abdul", "Password223", "Student"));
        repository.save(new User("Anita", "Password323", "Student"));
        repository.save(new User("Sepehr", "Password423","Student"));
        repository.save(new User("Thomas", "Password523","Student"));
        repository.save(new User("Professor1", "Password623","Professor"));

        // fetch all users
        System.out.println("Users found with findAll():");
        System.out.println("-------------------------------");
        for (User user : repository.findAll()) {
            System.out.println(user);
        }
        System.out.println();
    }
}








