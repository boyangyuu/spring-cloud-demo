package com.mobileenerlytics.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class Demo {
    @Id
    public ObjectId id;

    public String firstName;
    public String lastName;

    public List<String> projects = new ArrayList<>();

    public Demo() {}


    public Demo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void addProject(String projectId) {
        this.projects.add(projectId);
    }

    @Override
    public String toString() {
        return String.format(
                "Demo[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

    public ObjectId getId() {
        return id;
    }
}