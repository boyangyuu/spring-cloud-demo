package com.mobileenerlytics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("spring.data.mongodb")
public class DbProperties {
    private String uri;
    private String username;
    private String password;
    private String database;

    public String getUri() {
        return uri;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
