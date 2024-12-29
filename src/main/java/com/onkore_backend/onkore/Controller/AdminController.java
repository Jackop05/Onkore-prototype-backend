package com.onkore_backend.onkore.Controller;


import com.onkore_backend.onkore.Service.Authentification.PostAuthentificationServices;
import com.onkore_backend.onkore.Service.Data.GetServices;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private PostAuthentificationServices getAuthServices;

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
        return GetServices.getAdminData(request);
    }
}
