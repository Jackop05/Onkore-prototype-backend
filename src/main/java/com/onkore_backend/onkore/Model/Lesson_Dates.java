package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;

public class Lesson_Dates {
    @Id
    private String id;

    private String lessonDate;
    private String status;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setLessonDate(String lessonDate) {this.lessonDate = lessonDate;}
    public String getLessonDate() {return lessonDate;}

    public void setStatus(String status) {this.status = status;}
    public String getStatus() {return status;}
}
