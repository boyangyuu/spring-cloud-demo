package com.mobileenerlytics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

import java.util.*;

@EnableEurekaClient
@SpringBootApplication
// todo api gate, user regist, login =>token ... then authing
public class UserApplication {
    public static void main(String[] args) {
        System.out.println("user micro service is running");
    }

}
