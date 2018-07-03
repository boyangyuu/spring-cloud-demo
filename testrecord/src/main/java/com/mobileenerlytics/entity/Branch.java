package com.mobileenerlytics.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;


@Document(collection = "Branch")
public class Branch {
    @Id
    private ObjectId id;

    private String branchName;
//    private int totalTests; // should be useless
    private Date updatedMs;
    private ObjectId project_id;
    private Set<String> tests = new HashSet<>();

    private List<String> commits = new ArrayList<>();

    public Branch(String branchName, ObjectId project_id) {
        this.branchName = branchName;
        this.updatedMs = new Date();
        this.project_id = project_id;
    }
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Date getUpdatedMs() {
        return updatedMs;
    }

    public void setUpdatedMs(Date updatedMs) {
        this.updatedMs = updatedMs;
    }

    public List<String> getCommits() {
        return commits;
    }


    public Set<String> getTests() {
        return tests;
    }


    public void addTest(String testName) {
        // todo do it in repository
        if (tests.contains(testName)) {
            throw new RuntimeException("duplicate testNames" + testName + "in branch of" + id);
//            return;
        }
        tests.add(testName);
    }

    public ObjectId getProject_id() {
        return project_id;
    }

    public void setProject_id(ObjectId project_id) {
        this.project_id = project_id;
    }
}
