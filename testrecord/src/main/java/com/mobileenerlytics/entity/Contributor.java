package com.mobileenerlytics.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Contributor")
public class Contributor {

    @Id
    private String id;

    private String name;

    private Date updatedMs;

    public Contributor(String id, String name) {
        this.id = id;
        this.name = name;
        this.updatedMs = new Date();
    }


    public String getEmail() {
        return id;
    }

    public void setEmail(String email) {
        this.id = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }



}
