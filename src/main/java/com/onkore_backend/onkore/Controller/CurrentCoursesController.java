package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Data.DeleteServices;
import com.onkore_backend.onkore.Service.Data.PutServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/course")
public class CurrentCoursesController {

    @Autowired
    PutServices putServices;

    @Autowired
    DeleteServices deleteServices;

    @PutMapping("/put-topic")
    public String putTopic(@RequestBody Map<String, String> body) {
        try {
            putServices.putTopic(body.get("course_id"), body.get("topicName"));
            return "Topic added successfully";
        } catch (Exception e) {
            return  e.getMessage();
        }
    }

    @DeleteMapping("/delete-topic")
    public String deleteTopic(@RequestBody Map<String, String> body) {
        try {
            return deleteServices.deleteTopic(body.get("course_id"), body.get("topicName"));
        } catch (Exception e) {
            return  e.getMessage();
        }
    }

    
}
