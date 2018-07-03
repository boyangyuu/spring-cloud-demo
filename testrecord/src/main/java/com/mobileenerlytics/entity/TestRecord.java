package com.mobileenerlytics.entity;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.text.DecimalFormat;
import java.util.*;


@Document(collection = "TestRecord")
public class TestRecord {
    @Id
    private int id;

    private ObjectId commit_id;

    String testName;

    private double energy;

    private Date updatedMs;

    private Set<ThreadCompEnergy> threadCompEnergies = new HashSet<>();

    private Map<String, Double> componentUnit = new HashMap<>();

    private Map<String, Double> threadUnit = new HashMap<>();

    public TestRecord(String testName, ObjectId commit_id, Set<ThreadCompEnergy> threadCompEnergies) {
        this.testName = testName;
        this.updatedMs = new Date();
        this.setEnergy(computeTheEnergy(threadCompEnergies));
        this.commit_id = commit_id;
        this.setThreadCompEnergies(threadCompEnergies);
    }

    private double computeTheEnergy(Set<ThreadCompEnergy> threadCompEnergies) {
        Map<String, Double> cu = new HashMap<>();
        Map<String, Double> tu = new HashMap<>();
        double sumEnergy = 0.0;
        for (ThreadCompEnergy threadCompEnergy : threadCompEnergies) {
            if (!cu.containsKey(threadCompEnergy.componentId))
                cu.put(threadCompEnergy.componentId, 0.0);
            double componentEnergy = cu.get(threadCompEnergy.componentId);
            cu.put(threadCompEnergy.componentId, componentEnergy + threadCompEnergy.energy);
            if (!tu.containsKey(threadCompEnergy.threadId))
                tu.put(threadCompEnergy.threadId, 0.0);
            double threadEnergy = tu.get(threadCompEnergy.threadId);
            tu.put(threadCompEnergy.threadId, threadEnergy + threadCompEnergy.energy);
            sumEnergy += threadCompEnergy.energy;
        }
        this.setComponentUnit(cu);
        this.setThreadUnit(tu);
        DecimalFormat df2 = new DecimalFormat(".##");
        sumEnergy = Double.parseDouble(df2.format(sumEnergy));
        return sumEnergy;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getEnergy() {
        return this.energy;
    }

    public void setEnergy(double energy) {
        DecimalFormat df2 = new DecimalFormat(".##");
        this.energy = Double.parseDouble(df2.format(energy));
    }

    public Date getUpdatedMs() {
        return updatedMs;
    }

    public void setUpdatedMs(Date updatedMs) {
        this.updatedMs = updatedMs;
    }


    public Set<ThreadCompEnergy> getThreadCompEnergies() {
        return threadCompEnergies;
    }

    public void setThreadCompEnergies(final Set<ThreadCompEnergy> threadCompEnergies) {
        this.threadCompEnergies = threadCompEnergies;
    }

    public Map<String, Double> getComponentUnit() {
        return componentUnit;
    }

    public void setComponentUnit(Map<String, Double> componentUnit) {
        this.componentUnit = componentUnit;
    }

    public Map<String, Double> getThreadUnit() {
        return threadUnit;
    }

    public void setThreadUnit(Map<String, Double> threadUnit) {
        this.threadUnit = threadUnit;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

}
