package com.mobileenerlytics;


import com.mobileenerlytics.entity.Demo;
import com.mobileenerlytics.entity.Project;
import com.mobileenerlytics.repository.DemoRepository;
import com.mobileenerlytics.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@EnableEurekaClient
@RestController
@SpringBootApplication
public class TestRecordApplication implements CommandLineRunner {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private DemoRepository demoRepository;


    public static void main(String[] args) {
        SpringApplication.run(TestRecordApplication.class, args);
        System.out.println("testrecord microservice is running");
    }

    @Override
    public void run(String... strings) throws Exception {
        // https://spring.io/guides/tutorials/bookmarks/
        String projectName = "default";
        String userId = "1";
        demoRepository.deleteAll();
//        projectRepository.deleteAll();

        Project project = projectRepository.findProjectBy(projectName, userId);
        if (project == null) {
            project = new Project(projectName, userId);
            projectRepository.save(project);
        }

        // save a couple of customers
        Demo demo1 = new Demo("Alice", "Smith");
        demo1.addProject(project.getId());
        demoRepository.save(demo1);
        Demo demo2 = new Demo("Bob", "Smith");
        demo2.addProject(project.getId());
        demoRepository.save(demo2);
        project.addDemo(demo1.getId());
        project.addDemo(demo2.getId());
        projectRepository.save(project);

        // fetch all customers
        System.out.println("Customers found with findAll():");
        System.out.println("-------------------------------");
        for (Demo demo : demoRepository.findAll()) {
            System.out.println(demo);
        }
        System.out.println();

        // fetch an individual customer
        System.out.println("Customer found with findByFirstName('Alice'):");
        System.out.println("--------------------------------");
        System.out.println(demoRepository.findByFirstName("Alice"));

        System.out.println("Customers found with findByLastName('Smith'):");
        System.out.println("--------------------------------");
        for (Demo demo : demoRepository.findByLastName("Smith")) {
            System.out.println(demo);
        }

        //todo test with the old data, crud
        // todo add unit test https://spring.io/guides/gs/testing-web/
    }
}
