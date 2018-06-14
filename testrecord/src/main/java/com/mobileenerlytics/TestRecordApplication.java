package com.mobileenerlytics;


import com.mobileenerlytics.entity.Demo;
import com.mobileenerlytics.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@EnableEurekaClient
@SpringBootApplication
public class TestRecordApplication implements CommandLineRunner {

    @Autowired
    private ProjectRepository repository;


    public static void main(String[] args) {
        SpringApplication.run(TestRecordApplication.class, args);
        System.out.println("testrecord microservice is running");
    }

    @Override
    public void run(String... strings) throws Exception {
        // https://spring.io/guides/tutorials/bookmarks/
        repository.deleteAll();

        // save a couple of customers
        repository.save(new Demo("Alice", "Smith"));
        repository.save(new Demo("Bob", "Smith"));

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Demo demo : repository.findAll()) {
            System.out.println(demo);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(repository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Demo demo : repository.findByLastName("Smith")) {
            System.out.println(demo);
        }

    }
}
