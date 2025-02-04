package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "new_courses")
public class New_Course {
    @Id
    private String id;

    private String username;
    private String description;
    private String subject;
    private String additionalInfo;

    @DBRef
    private Subject_Course subjectCourse;

    @DBRef
    private Current_Course currentCourse;

    @DBRef
    private List<Admin> admins;

    @DBRef
    private List<Lesson_Dates> lessonDates;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setSubject(String subject) {this.subject = subject;}
    public String getSubject() {return subject;}

    public void setAdditionalInfo(String additionalInfo) {this.additionalInfo = additionalInfo;}
    public String getAdditionalInfo() {return additionalInfo;}

    public void setSubjectCourse(Subject_Course subjectCourse) {this.subjectCourse = subjectCourse;}
    public Subject_Course getSubjectCourse() {return subjectCourse;}

    public void setCurrentCourse(Current_Course currentCourse) {this.currentCourse = currentCourse;}
    public Current_Course getCurrentCourse() {return currentCourse;}

    public void setAdmins(List<Admin> admins) {this.admins = admins;}
    public List<Admin> getAdmins() {return admins;}

    public List<Lesson_Dates> getLessonDates() {return lessonDates;}
    public void setLessonDates(List<Lesson_Dates> lessonDates) {this.lessonDates = lessonDates;}
}
