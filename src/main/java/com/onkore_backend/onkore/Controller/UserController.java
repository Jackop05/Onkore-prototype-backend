package com.onkore_backend.onkore.Controller;

import com.onkore_backend.onkore.Service.Authentification.GetAuthentificationServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private GetAuthentificationServices getAuthServices;

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
}
