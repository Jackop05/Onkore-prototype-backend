package com.onkore_backend.onkore.Util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Date;

public class JsonWebToken {
    private static final String SECRET_KEY = "SecretKeyWithMoreLettersAndMoreWordsIncluded123456789";
    private static final long EXPIRATION_TIME = 86400000;

    public static String generateToken(String username, String email, String role) {
        return Jwts.builder()
                .setSubject(username)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public static void setJwtCookie(HttpServletResponse response, String jwtToken) {
        Cookie jwtCookie = new Cookie("jwt_auth_token", jwtToken);

        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(3600);

        response.addCookie(jwtCookie);
    }
}
