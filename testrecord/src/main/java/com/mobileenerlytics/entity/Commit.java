package com.mobileenerlytics.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "Commit")
public class Commit {
    @Id
    private ObjectId id;

    private String hash;

    private String desc;

    private String contributor_email;

    private ObjectId branch_id;

    private String branchName;

    private ObjectId project_id;

    private Date updatedMs;

    private Set<Integer> testRecords = new HashSet<>();

    private String jobStatus;

    private Long jobDurationMs;

    public Commit(String hash, Branch branch, Date updatedMs, String desc, String contributor_email) {
        this.hash = hash;
        this.updatedMs = updatedMs;
        this.desc = desc;
        this.contributor_email = contributor_email;
        this.branch_id = branch.getId();
        this.branchName = branch.getBranchName();
        this.project_id = branch.getProject_id();
    }

    public Commit() {}

    public void addTestRecord(Integer testRecordId) {
        testRecords.add(testRecordId);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getContributor_email() {
        return contributor_email;
    }

    public void setContributor_email(String contributor_email) {
        this.contributor_email = contributor_email;
    }

    public ObjectId getBranch_id() {
        return branch_id;
    }

    public void setBranch_id(ObjectId branch_id) {
        this.branch_id = branch_id;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public ObjectId getProject_id() {
        return project_id;
    }

    public void setProject_id(ObjectId project_id) {
        this.project_id = project_id;
    }

    public Date getUpdatedMs() {
        return updatedMs;
    }

    public void setUpdatedMs(Date updatedMs) {
        this.updatedMs = updatedMs;
    }

    public Set<Integer> getTestRecords() {
        return testRecords;
    }

    public void setTestRecords(Set<Integer> testRecords) {
        this.testRecords = testRecords;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public Long getJobDurationMs() {
        return jobDurationMs;
    }

    public void setJobDurationMs(Long jobDurationMs) {
        this.jobDurationMs = jobDurationMs;
    }
}
