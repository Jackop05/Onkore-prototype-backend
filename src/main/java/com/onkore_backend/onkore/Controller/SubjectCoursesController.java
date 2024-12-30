package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Model.Subject_Course;
import com.onkore_backend.onkore.Service.Authentification.AuthentificationServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/subject-courses")
public class SubjectCoursesController {

    @Autowired
    private GetServices getServices;

    @GetMapping("/get-subject-courses")
    public List<Subject_Course> getSubjectCourses() {
        return getServices.getSubjectCoursesData();
    }
}
