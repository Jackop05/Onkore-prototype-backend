package com.onkore_backend.onkore.Service.Data;

import com.onkore_backend.onkore.Repository.SubjectCourseRepository;
import com.onkore_backend.onkore.Model.Subject_Course;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.onkore_backend.onkore.Util.JsonWebToken.getTokenDataFromCookie;

@Service
public class GetServices {

    @Autowired
    private SubjectCourseRepository subjectCourseRepository;

    public static Map<String, Object> getUserData(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);

        if (claims != null) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", claims.get("id"));
            studentData.put("username", claims.get("username"));
            studentData.put("email", claims.get("email"));
            studentData.put("currentCourses", claims.get("currentCourses"));
            studentData.put("role", claims.get("role"));
            return studentData;
        } else {
            return null;
        }
    }

    public static Map<String, Object> getAdminData(HttpServletRequest request) {
        Claims claims = getTokenDataFromCookie(request);

        if (claims != null) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("id", claims.get("id"));
            studentData.put("username", claims.get("username"));
            studentData.put("email", claims.get("email"));
            studentData.put("description", claims.get("description"));
            studentData.put("contact", claims.get("contact"));
            studentData.put("availability", claims.get("availability"));
            studentData.put("currentCourses", claims.get("currentCourses"));
            studentData.put("newCourses", claims.get("newCourses"));
            studentData.put("role", claims.get("role"));
            return studentData;
        } else {
            return null;
        }
    }

    public void getNewCoursesData() {
        // Logic to fetch new courses data
    }


    public List<Subject_Course> getSubjectCoursesData() {
        return subjectCourseRepository.findAll();
    }


    public void getAvailableDates() {

    }
}
