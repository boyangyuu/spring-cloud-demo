package com.mobileenerlytics.repository;

import com.mobileenerlytics.entity.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository repository;

    @Before
    public void setUp() throws Exception {
        repository.deleteAll();
    }

    @Test
    public void saveProject() {
        Project project = new Project();
        project.setName("default");
        project.setUserId("1");
        repository.save(project);

    }
    @Test
    public void findProjectBy() throws Exception {
        Project project = repository.findProjectBy("default", "1");
        Assert.assertNotNull(project);
    }

}