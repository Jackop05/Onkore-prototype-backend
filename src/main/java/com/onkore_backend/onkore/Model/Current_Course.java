package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "current_courses")
public class Current_Course {
    @Id
    private String id;

    private String subject;
    private String description;
    private String username;
    private Integer price;
    private String[] topics;
    //      private PDFs[] materials;


    @DBRef
    private List<Lesson_Dates> lessonDates;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setSubject(String subject) {this.subject = subject;}
    public String getSubject() {return subject;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setUsername(String username) {this.username = username;}
    public String getUsername() {return username;}

    public void setPrice(Integer price) {this.price = price;}
    public Integer getPrice() {return price;}

    public void setTopics(String[] topics) {this.topics = topics;}
    public String[] getTopics() {return topics;}

    public List<Lesson_Dates> getLessonDates() {return lessonDates;}
    public void setLessonDates(List<Lesson_Dates> lessonDates) {this.lessonDates = lessonDates;}
}
