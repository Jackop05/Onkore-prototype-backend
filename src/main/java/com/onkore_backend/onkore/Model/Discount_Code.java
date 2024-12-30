package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Document(collection = "discount_codes")
public class Discount_Code {
    @Id
    private String id;

    private String code;
    private Date beginsAt;
    private Date expiresAt;
    private Integer discountPercentage;
    private Integer discountAmount;

    private List<String> subjects;
    private List<String> emails;


    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public void setCode(String code) {this.code = code;}
    public String getCode() {return code;}

    public void setBeginsAt(Date beginsAt) {this.beginsAt = beginsAt;}
    public Date getBeginsAt() {return beginsAt;}

    public void setExpiresAt(Date expiresAt) {this.expiresAt = expiresAt;}
    public Date getExpiresAt() {return expiresAt;}

    public void setDiscountPercentage(Integer discountPercentage) {this.discountPercentage = discountPercentage;}
    public Integer getDiscountPercentage() {return discountPercentage;}

    public void setDiscountAmount(Integer discountAmount) {this.discountAmount = discountAmount;}
    public Integer getDiscountAmount() {return discountAmount;}

    public void setSubjects(List<String> subjects) {this.subjects = subjects;}
    public List<String> getSubjects() {return subjects;}

    public void setEmails(List<String> emails) {this.emails = emails;}
    public List<String> getEmails() {return emails;}
}
