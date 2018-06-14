package com.mobileenerlytics.entity;

import org.springframework.data.annotation.Id;

public class Demo {
    @Id
    public String id;

    public String firstName;
    public String lastName;

    public Demo() {}

    public Demo(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return String.format(
                "Demo[id=%s, firstName='%s', lastName='%s']",
                id, firstName, lastName);
    }

}