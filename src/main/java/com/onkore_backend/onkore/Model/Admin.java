package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Document(collection = "admins")
public class Admin {
    @Id
    private String id;

    private String username;
    private String email;
    private String password;
    private String description;
    private String contact;
    private String resetPasswordToken;
    private List<String> subjectTeachingList;
    // private Image image;

    @DBRef
    private List<Availability> availability;

    @DBRef
    private List<Current_Course> currentCourses;

    @DBRef
    private List<New_Course> newCourses;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setEmail(String email) {this.email = email;}
    public String getEmail() {return email;}

    public void setPassword(String password) {this.password = password;}
    public String getPassword() {return password;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setContact(String contact) {this.contact = contact;}
    public String getContact() {return contact;}

    public void setResetPasswordToken(String resetPasswordToken) {this.resetPasswordToken = resetPasswordToken;}
    public String getResetPasswordToken() {return resetPasswordToken;}

    public void setSubjectTeachingList(List<String> subjectTeachingList) {this.subjectTeachingList = subjectTeachingList;}
    public List<String> getSubjectTeachingList() {return subjectTeachingList;}

    public void setAvailability(List<Availability> availability) {this.availability = availability;}
    public List<Availability> getAvailability() {return availability;}

    public void setCurrentCourses(List<Current_Course> currentCourses) {this.currentCourses = currentCourses;}
    public List<Current_Course> getCurrentCourses() {return currentCourses;}

    public void setNewCourses(List<New_Course> newCourses) {this.newCourses = newCourses;}
    public List<New_Course> getNewCourses() {return newCourses;}
}
