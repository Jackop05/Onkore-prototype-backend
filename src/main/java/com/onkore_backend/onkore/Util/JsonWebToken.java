package com.onkore_backend.onkore.Util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;
import java.util.List;

public class JsonWebToken {
    private static final String SECRET_KEY = "SecretKeyWithMoreLettersAndMoreWordsIncluded123456789";
    private static final long EXPIRATION_TIME = 86400000;

    public static String generateUserToken(String id, String username, String email, List currentCourses, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .claim("currentCourses", currentCourses)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static String generateAdminToken(String id, String username, String email, String description, String contact, List availability, List currentCourses, List newCourses, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", id)
                .claim("username", username)
                .claim("email", email)
                .claim("contact", contact)
                .claim("description", description)
                .claim("availability", availability)
                .claim("currentCourses", currentCourses)
                .claim("newCourses", newCourses)
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
                if ("jwt_auth_token".equals(cookie.getName())) {
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
        Cookie jwtCookie = new Cookie("jwt_auth_token", jwtToken);

        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        response.addCookie(jwtCookie);
    }

    public static void deleteJwtCookie(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt_auth_token", null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);

        response.addCookie(jwtCookie);
    }
}
