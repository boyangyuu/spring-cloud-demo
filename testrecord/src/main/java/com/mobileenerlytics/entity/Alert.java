package com.mobileenerlytics.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Alert {
    @Id
    private int id;

    private String project_id;

    String testName;

    private String branchName;

    private Date endMs;

    private Date updatedMs;

}
