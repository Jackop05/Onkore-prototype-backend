package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Authentification.AuthentificationServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Util.JsonFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private AuthentificationServices getAuthServices;

    @Autowired
    private GetServices getServices;

    @Autowired
    private PostServices postServices;

    @PostMapping("/register-user")
    public String registerUser(@RequestBody Map<String, String> body) {
        try {
            getAuthServices.RegisterUser(body.get("username"), body.get("email"), body.get("password"));
            return "User registered successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login-user")
    public String loginUser(@RequestBody Map<String, String> body, HttpServletResponse response) {
        try {
            getAuthServices.LoginUser(body.get("email"), body.get("password"), response);
            return "User logged in successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/logout-user")
    public String loginUser(HttpServletResponse response) {
        try {
            getAuthServices.LogoutUser(response);
            return "User logged out successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/get-user-data")
    public Map getUserData(HttpServletRequest request) {
        return getServices.getUserData(request);
    }

    @PostMapping("/post-course")
    public String postCourse(@RequestBody Map<String, String> body) {
        try {
            String userId = body.get("user_id");
            String courseId = body.get("course_id");
            String datesString = body.get("dates");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            List<Date> dateList = JsonFormatter.convertStringToList(datesString, dateStr -> {
                try {
                    return dateFormat.parse(dateStr);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid date format: " + dateStr);
                }
            });

            postServices.postCourse(userId, courseId, dateList);
            return "Course posted successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
