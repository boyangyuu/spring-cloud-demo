package com.mobileenerlytics.entity;

import org.bouncycastle.jcajce.provider.digest.SHA3;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


//todo api doc : https://spring.io/guides/gs/testing-restdocs/

//todo try put a demo in project, see what happened,

@Document(collection = "User")
public class User {
    @Id
    private String id;

    private String email;

    private String username;

    private String role;

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    private String lastname;
    private String firstname;

    private String password;
    private String organization;

    private Date registrationTime;

    private String payCustomerId; // set as "DebugPayId" will not need pay.

    private Date expiredDate; // if expiredDate exist, then will be idenified contrator customer

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public Date getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(Date registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getExpiredDate() {
        return expiredDate;
    }

    public void setExpiredDate(Date expiredDate) {
        this.expiredDate = expiredDate;
    }

    public String getRole() {
        if (this.role == null) return "default";
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public User(String email, String username, String firstname, String lastname,  String password, String organization, Date registrationTime
                ) {
        this.email = email;
        this.username = username;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = hashPassword(password);
        this.organization = organization;
        this.registrationTime = registrationTime;
    }

    public User() {
    }

    public boolean isPassword(String givenPassword) {
        String hashedPassword = hashPassword(givenPassword);
        return password.equals(hashedPassword);
    }

    public static String hashPassword(String password_) {
        final SHA3.DigestSHA3 md = new SHA3.DigestSHA3(256);
        md.update(password_.getBytes());
        return Hex.toHexString(md.digest());
    }


    public String getPayCustomerId() {
        return payCustomerId;
    }

    public void setPayCustomerId(String payCustomerId) {
        this.payCustomerId = payCustomerId;
    }

}
