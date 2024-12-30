package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;

@Document(collection = "availability")
public class Availability {
    @Id
    private String id;

    private String weekday;
    private LocalTime hourStart;
    private LocalTime hourEnd;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setWeekday(String weekday) {this.weekday = weekday;}
    public String getWeekday() {return weekday;}

    public void setHourStart(LocalTime hourStart) {this.hourStart = hourStart;}
    public LocalTime getHourStart() {return hourStart;}

    public void setHourEnd(LocalTime hourEnd) {this.hourEnd = hourEnd;}
    public LocalTime getHourEnd() {return hourEnd;}
}
