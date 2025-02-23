package com.onkore_backend.onkore.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "materials")
public class Material {
    @Id
    private String id;

    private String originalFilename;
    private String filePath;

    public void setId(String id) {this.id = id;}
    public String getId() {return id;}

    public String getOriginalFilename() {return originalFilename;}
    public void setOriginalFilename(String originalFilename) {this.originalFilename = originalFilename;}

    public String getFilePath() {return filePath;}
    public void setFilePath(String filePath) {this.filePath = filePath;}
}
