package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "current_courses")
public class Current_Course {
    @Id
    private String id;

    private String subject;
    private String description;
    private String level;
    private String username;
    private Integer price;
    private Integer iconIndex;
    private ArrayList<String> topics;

    @DBRef
    private Subject_Course subjectCourse;

    @DBRef
    private List<Lesson_Dates> lessonDates;

    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setSubject(String subject) {this.subject = subject;}
    public String getSubject() {return subject;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setLevel(String level) {this.level = level;}
    public String getLevel() {return level;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setPrice(Integer price) {this.price = price;}
    public Integer getPrice() {return price;}

    public void setIconIndex(Integer iconIndex) {this.iconIndex = iconIndex;}
    public Integer getIconIndex() {return iconIndex;}

    public void setTopics(ArrayList<String> topics) {this.topics = topics;}
    public ArrayList<String> getTopics() {return topics;}

    public void setSubjectCourse(Subject_Course subjectCourse) {this.subjectCourse = subjectCourse;}
    public Subject_Course getSubjectCourse() {return subjectCourse;}

    public void setLessonDates(List<Lesson_Dates> lessonDates) {this.lessonDates = lessonDates;}
    public List<Lesson_Dates> getLessonDates() {return lessonDates;}
}
