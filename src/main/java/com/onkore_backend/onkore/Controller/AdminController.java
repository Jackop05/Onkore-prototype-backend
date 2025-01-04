package com.onkore_backend.onkore.Controller;


import com.onkore_backend.onkore.Service.Authentification.AuthentificationServices;
import com.onkore_backend.onkore.Service.Data.DeleteServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Service.Data.PutServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private AuthentificationServices getAuthServices;

    @Autowired
    private PutServices putServices;

    @Autowired
    private GetServices getServices;
    @Autowired
    private PostServices postServices;
    @Autowired
    private DeleteServices deleteServices;

    @PostMapping("/register-admin")
    public String RegisterAdmin(@RequestBody Map<String, String> body) {
        try {
            getAuthServices.RegisterAdmin(body.get("username"), body.get("email"), body.get("password"), body.get("description"), body.get("contact"));
            return "Admin registered successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/login-admin")
    public String LoginAdmin(@RequestBody Map<String, String> body, HttpServletResponse response) {
        try {
            getAuthServices.LoginAdmin(body.get("email"), body.get("password"), response);
            return "Admin logged in successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/logout-admin")
    public String LoginAdmin(HttpServletResponse response) {
        try {
            getAuthServices.LogoutAdmin(response);
            return "Admin logged out successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/get-admin-data")
    public Map getUserData(HttpServletRequest request) {
        return getServices.getAdminData(request);
    }

    @PutMapping("/post-availability")
    public String postAvailability(@RequestBody Map<String, String> body) {
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startHour = LocalTime.parse(body.get("startHour"), timeFormatter);
            LocalTime endHour = LocalTime.parse(body.get("endHour"), timeFormatter);

            putServices.putAvailability(body.get("admin_id"), startHour, endHour, body.get("weekday"));
            return "Availability posted successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/get-full-availability")
    public Map getFullAvailability(HttpServletResponse response) {
        return getServices.getAllAvailableDates();
    }

    @GetMapping("/get-availability")
    public Map getAvailability(HttpServletResponse response) {
        return getServices.getReducedAvailableDates();
    }

    @PutMapping("/put-lesson-status")
    public String putLessonStatus(@RequestBody Map<String, String> body) {
        try {
            return putServices.putLessonStatus(body.get("course_id"), body.get("lesson_id"), body.get("status"));
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @PostMapping("/handle-new-course")
    public String handleNewCourse(@RequestBody Map<String, String> body) {
        try {
            return postServices.handleNewCourse(body.get("course_id"), body.get("admin_id"), body.get("action"));
        } catch(Exception e) {
            return e.getMessage();
        }
    }

    @DeleteMapping("/delete-availability")
    public String deleteAvailability(@RequestBody Map<String, String> body) {
        try {
            return deleteServices.deleteAdminAvailability(body.get("admin_id"), body.get("availability_id"));
        } catch(Exception e) {
            return e.getMessage();
        }
    }
}
