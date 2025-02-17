package com.onkore_backend.onkore.Service.Authentification;

import com.onkore_backend.onkore.Model.*;
import com.onkore_backend.onkore.Repository.AdminRepository;
import com.onkore_backend.onkore.Repository.UserRepository;
import com.onkore_backend.onkore.Util.JsonWebToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AuthentificationServices {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void RegisterUser(String username, String email, String password) {
        if (username == null || email == null || password == null) {
            throw new IllegalArgumentException("Nazwa użytkownika, email oraz hasło nie mogą być puste.");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Ten email jest już zarejestrowany.");
        }
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Ta nazwa użytkownika jest już zarejestrowana");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCurrentCourses(new ArrayList<Current_Course>());

        userRepository.save(user);
    }

    public ResponseEntity<String> LoginUser(String email, String password, HttpServletResponse response) {
        if (email == null || password == null) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User with email not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = JsonWebToken.generateUserToken(user.getId(), user.getUsername(), user.getEmail(), "user");
        JsonWebToken.setJwtCookie(response, token);

        return null;
    }

    public void RegisterAdmin(String username, String email, String password, String description, String contact) {
        if (username == null || email == null || password == null || contact == null) {
            throw new IllegalArgumentException("Username, email, password and contact must not be null.");
        }

        if (adminRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already registered.");
        }

        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setDescription(description);
        admin.setContact(contact);
        admin.setSubjectTeachingList(new ArrayList<String>());
        admin.setAvailability(new ArrayList<Availability>());
        admin.setCurrentCourses(new ArrayList<Current_Course>());
        admin.setNewCourses(new ArrayList<New_Course>());

        adminRepository.save(admin);
    }

    public ResponseEntity<String> LoginAdmin(String email, String password, HttpServletResponse response) {
        if (email == null || password == null) {
            return ResponseEntity.status(401).body("Invalid email or password");
        }

        Admin admin = (Admin) adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin with email not found"));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = JsonWebToken.generateAdminToken(admin.getId(), admin.getUsername(), admin.getEmail(), admin.getDescription(), admin.getContact(), admin.getAvailability(), admin.getCurrentCourses(), admin.getNewCourses(), "admin");
        JsonWebToken.setJwtCookie(response, token);

        return null;
    }

    public void LogoutUser(HttpServletResponse response) {
        JsonWebToken.deleteJwtCookie(response);
    }

    public void LogoutAdmin(HttpServletResponse response) {
        JsonWebToken.deleteJwtCookie(response);
    }
}
