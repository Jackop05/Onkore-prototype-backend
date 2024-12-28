package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document(collection = "discount_codes")
public class Discount_Code {
    @Id
    private String id;

    private String code;
    private LocalDate beginsAt;
    private LocalDate expiresAt;

    private List<String> subjects;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setCode(String code) {this.code = code;}
    public String getCode() {return code;}

    public void setBeginsAt(LocalDate beginsAt) {this.beginsAt = beginsAt;}
    public LocalDate getBeginsAt() {return beginsAt;}

    public void setExpiresAt(LocalDate expiresAt) {this.expiresAt = expiresAt;}
    public LocalDate getExpiresAt() {return expiresAt;}

    public void setSubjects(List<String> subjects) {this.subjects = subjects;}
    public List<String> getSubjects() {return subjects;}
}
