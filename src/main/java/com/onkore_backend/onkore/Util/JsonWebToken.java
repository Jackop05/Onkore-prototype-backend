package com.onkore_backend.onkore.Util;

import com.onkore_backend.onkore.Model.*;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonWebToken {
    private static final String SECRET_KEY = "SecretKeyWithMoreLettersAndMoreWordsIncluded123456789";
    private static final long EXPIRATION_TIME = 86400000;

    private static String jwtName = "jwt_auth_token";

    public static String generateUserToken(String id, String username, String email, String role) {
        System.out.println("New date to see: " + new Date(System.currentTimeMillis() + EXPIRATION_TIME));

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String generateAdminToken(String id, String username, String email, String description, String contact, List<Availability> availability, List<Current_Course> currentCourses, List<New_Course> newCourses, String role) {
        List<Map<String, String>> serializedAvailability = availability.stream()
                .map(item -> Map.of(
                        "id", item.getId(),
                        "weekday", item.getWeekday(),
                        "hourStart", item.getHourStart().toString(),
                        "hourEnd", item.getHourEnd().toString()
                ))
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .claim("contact", contact)
                .claim("description", description)
                .claim("availability", serializedAvailability)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static Claims getTokenDataFromCookie(HttpServletRequest request) {
        String jwtToken = null;
        Cookie[] cookies = request.getCookies();

        // Extract JWT token from cookies
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (jwtName.equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break; // Exit loop once the token is found
                }
            }
        }

        // Return null if the token is not found
        if (jwtToken == null) {
            System.out.println("JWT token not found in cookies.");
            return null;
        }

        // Decode and validate the token
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(jwtToken)
                    .getBody(); // Return the claims
        } catch (Exception e) {
            System.out.println("Invalid or expired JWT token: " + e.getMessage());
            return null;
        }
    }


    public static void setJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie(jwtName, jwtToken);

        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        response.addCookie(jwtCookie);
    }

    public static void deleteJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie(jwtName, null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
    }

    private static List<Map<String, Object>> serializeLessonDatesToString(List<Lesson_Dates> lessonDates) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
        return lessonDates.stream()
                .map(lessonDate -> {
                    Map<String, Object> serialized = new HashMap<>();
                    serialized.put("id", lessonDate.getId());
                    serialized.put("lessonDate", dateFormatter.format(lessonDate.getLessonDate()));
                    serialized.put("status", lessonDate.getStatus());
                    return serialized;
                })
                .collect(Collectors.toList());
    }

    private static Map<String, Object> serializeSubjectCourseToString(Subject_Course subjectCourse) {
        Map<String, Object> serialized = new HashMap<>();
        serialized.put("id", subjectCourse.getId());
        serialized.put("subject", subjectCourse.getSubject());
        serialized.put("level", subjectCourse.getLevel());
        serialized.put("description", subjectCourse.getDescription());
        serialized.put("price", subjectCourse.getPrice());
        serialized.put("iconIndex", subjectCourse.getIconIndex());
        return serialized;
    }
}
