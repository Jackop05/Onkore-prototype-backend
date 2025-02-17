package com.onkore_backend.onkore.Controller;


import com.onkore_backend.onkore.Service.Authentification.AuthentificationServices;
import com.onkore_backend.onkore.Service.Data.DeleteServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Service.Data.PutServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
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
    public ResponseEntity<Map<String, String>> RegisterAdmin(@RequestBody Map<String, String> body) {
        try {
            getAuthServices.RegisterAdmin(
                    body.get("username"),
                    body.get("email"),
                    body.get("password"),
                    body.get("description"),
                    body.get("contact")
            );

            // ✅ Return JSON response
            return ResponseEntity.ok(Collections.singletonMap("message", "Admin registered successfully"));

        } catch (IllegalArgumentException e) {
            // Return JSON error with 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Return JSON error with 500 Internal Server Error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Unexpected error occurred: " + e.getMessage()));
        }
    }


    @PostMapping("/login-admin")
    public ResponseEntity LoginAdmin(@RequestBody Map<String, String> body, HttpServletResponse response) {

        try {
            getAuthServices.LoginAdmin(body.get("email"), body.get("password"), response);
            return ResponseEntity.ok(Collections.singletonMap("message", "Zalogowano pomyślnie administratora"));
        } catch (IllegalArgumentException e) {
            // Return HTTP 401 if authentication fails (e.g., wrong password or non-existing account)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Return HTTP 500 for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred."));
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
        System.out.println(body);
        try {
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime startHour = LocalTime.parse(body.get("hourStart"), timeFormatter);
            LocalTime endHour = LocalTime.parse(body.get("hourEnd"), timeFormatter);

            putServices.putAvailability(body.get("admin_id"), startHour, endHour, body.get("weekday"));
            return "Availability posted successfully";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @GetMapping("/get-all-admins-data")
    public List<Map<String, Object>> getAllAdminsData() {
        return getServices.getAllAdminData();
    }

    @GetMapping("/get-full-availability")
    public Map getFullAvailability(HttpServletResponse response) {
        return getServices.getAllAvailableDates();
    }

    @GetMapping("/get-availability")
    public ResponseEntity<Map<String, Object>> getAvailability(@RequestParam String adminId) {
        try {
            Map<String, Object> availabilityData = getServices.getAvailability(adminId);
            return ResponseEntity.ok(availabilityData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An error occurred: " + e.getMessage()));
        }
    }

    @GetMapping("/get-admin-current-courses")
    public ResponseEntity<?> getUserCurrentCourses(HttpServletRequest request) {
        try {
            List<Map<String, Object>> courses = getServices.getAdminCurrentCourses(request);
            System.out.println("Courses: " + courses);
            if (courses == null || courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No current courses found for admin"));
            }

            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
    }

    @PutMapping("/update-lesson-link")
    public ResponseEntity<String> updateLessonLink(@RequestBody Map<String, String> requestData) {
        String courseId = requestData.get("courseId");
        String lessonId = requestData.get("lessonId");
        String link = requestData.get("link");

        putServices.updateLessonLink(courseId, lessonId, link);
        return ResponseEntity.ok("Lesson link updated successfully");
    }

    @DeleteMapping("/cancel-lesson")
    public ResponseEntity<String> cancelLesson(@RequestBody Map<String, String> requestData) {
        String courseId = requestData.get("courseId");
        String lessonId = requestData.get("lessonId");

        deleteServices.cancelLesson(courseId, lessonId);
        return ResponseEntity.ok("Lesson canceled successfully");
    }

    @PutMapping("/update-lesson-status")
    public ResponseEntity<String> updateLessonStatus(@RequestBody Map<String, String> requestData) {
        String courseId = requestData.get("courseId");
        String lessonId = requestData.get("lessonId");
        String newStatus = requestData.get("status");

        putServices.updateLessonStatus(courseId, lessonId, newStatus);
        return ResponseEntity.ok("Lesson status updated successfully");
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
            postServices.handleNewCourse(body.get("course_id"), body.get("admin_id"));
            return "Course handled successfully";
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
