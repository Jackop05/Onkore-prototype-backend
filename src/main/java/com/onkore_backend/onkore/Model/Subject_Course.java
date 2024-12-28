package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subject_courses")
public class Subject_Course {
    @Id
    private String id;

    private String subject;
    private String description;
    private Integer price;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setSubject(String subject) {this.subject = subject;}
    public String getSubject() {return subject;}

    public void setDescription(String description) {this.description = description;}
    public String getDescription() {return description;}

    public void setPrice(Integer price) {this.price = price;}
    public Integer getPrice() {return price;}
}
