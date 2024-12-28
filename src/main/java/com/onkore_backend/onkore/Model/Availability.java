package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "availability")
public class Availability {
    @Id
    private String id;

    private String weekday;
    private String hourStart;
    private String hourEnd;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setWeekday(String weekday) {this.weekday = weekday;}
    public String getWeekday() {return weekday;}

    public void setHourStart(String hourStart) {this.hourStart = hourStart;}
    public String getHourStart() {return hourStart;}

    public void setHourEnd(String hourEnd) {this.hourEnd = hourEnd;}
    public String getHourEnd() {return hourEnd;}
}
