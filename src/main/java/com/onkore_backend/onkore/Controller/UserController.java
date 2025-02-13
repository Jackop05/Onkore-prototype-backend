package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Authentification.AuthentificationServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import com.onkore_backend.onkore.Service.Data.PostServices;
import com.onkore_backend.onkore.Util.JsonFormatter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.text.SimpleDateFormat;
import java.util.*;

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
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, String> body) {
        try {
            getAuthServices.RegisterUser(body.get("username"), body.get("email"), body.get("password"));
            return ResponseEntity.ok(Collections.singletonMap("message", "Zarejestrowano pomyślnie użytkownik ."));
        } catch (IllegalArgumentException e) {
            // Return HTTP 400 for client-side errors (e.g., email already registered)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Return HTTP 500 for unexpected server errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
    }

    @PostMapping("/login-user")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, String> body, HttpServletResponse response) {
        try {
            getAuthServices.LoginUser(body.get("email"), body.get("password"), response);
            return ResponseEntity.ok(Collections.singletonMap("message", "Zalogowano pomyślnie użytkownika"));
        } catch (IllegalArgumentException e) {
            // Return HTTP 401 if authentication fails (e.g., wrong password or non-existing account)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            // Return HTTP 500 for unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
    }

    @GetMapping("/get-user-current-courses")
    public ResponseEntity<?> getUserCurrentCourses(HttpServletRequest request) {
        try {
            List<Map<String, Object>> courses = getServices.getUserCurrentCourses(request);
            if (courses == null || courses.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.singletonMap("error", "No current courses found for user"));
            }
            return ResponseEntity.ok(courses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "An unexpected error occurred."));
        }
    }

    @GetMapping("/get-single-user-current-course")
    public ResponseEntity<?> getSingleUserCurrentCourse(@RequestParam String courseId, HttpServletRequest request) {
        try {
            Map<String, Object> course = getServices.getSingleUserCurrentCourse(request, courseId);
            if (course == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No current courses found for user"));
            }
            System.out.println(course);
            return ResponseEntity.ok(course);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "An unexpected error occurred."));
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
    public ResponseEntity<?> getUserData(HttpServletRequest request) {
        try {
            Map<String, Object> userData = getServices.getUserData(request);
            if (userData == null || userData.isEmpty()) {
                return ResponseEntity.status(404).body(Map.of("error", "User not found"));
            }
            return ResponseEntity.ok(userData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error", "message", e.getMessage()));
        }
    }

    @PostMapping("/post-course")
    public ResponseEntity<Map<String, String>> postCourse(@RequestBody Map<String, String> body) {
        Map<String, String> response = new HashMap<>();
        try {
            String userId = body.get("user_id");
            String courseId = body.get("course_id");
            String datesString = body.get("dates");
            String bonusInfo = body.get("bonus_info");
            String promoCode = body.get("promo_code");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
            List<Date> dateList = JsonFormatter.convertStringToList(datesString, dateStr -> {
                try {
                    return dateFormat.parse(dateStr);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid date format: " + dateStr);
                }
            });

            postServices.postCourse(userId, courseId, dateList, bonusInfo);

            response.put("message", "Course posted successfully");
            return ResponseEntity.ok(response); // ✅ Returns JSON response

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response); // ✅ Returns JSON error response
        }
    }

}
