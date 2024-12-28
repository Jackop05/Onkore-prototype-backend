package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;

public class Lesson_Dates {
    @Id
    private String id;

    private String lessonDate;
    private Boolean accepted;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setLessonDate(String lessonDate) {this.lessonDate = lessonDate;}
    public String getLessonDate() {return lessonDate;}

    public void setAccepted(Boolean accepted) {this.accepted = accepted;}
    public Boolean getAccepted() {return accepted;}
}
