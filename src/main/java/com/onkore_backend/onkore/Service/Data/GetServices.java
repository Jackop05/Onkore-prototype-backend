package com.onkore_backend.onkore.Service.Data;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;

import static com.onkore_backend.onkore.Util.JsonWebToken.getTokenDataFromCookie;

public class GetServices {

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

    public void getAdminData() {
        // Logic to fetch admin data
    }

    public static void getUserData() {
        // Logic to fetch user data
    }

    public void getNewCoursesData() {
        // Logic to fetch new courses data
    }

    public void getUserCourseData() {
        // Logic to fetch user-course data
    }

    public void getSubjectCoursesData() {
        // Logic to fetch subject-specific course data
    }

    public void getAvailableDates() {
        // Logic to fetch available dates
    }
}
