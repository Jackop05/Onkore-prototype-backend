package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "users")
public class User {
    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String resetPasswordToken;

    @DBRef
    private List<Current_Course> currentCourses;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}

    public void setResetPasswordToken(String resetPasswordToken) {this.resetPasswordToken = resetPasswordToken;}
    public String getResetPasswordToken() {return resetPasswordToken;}

    public void setCurrentCourses(List<Current_Course> currentCourses) { this.currentCourses = currentCourses;}
    public List<Current_Course> getCurrentCourses() { return currentCourses;}
}
