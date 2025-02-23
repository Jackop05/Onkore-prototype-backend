package com.onkore_backend.onkore.Service.Authentification;

import com.onkore_backend.onkore.Model.Admin;
import com.onkore_backend.onkore.Model.User;
import com.onkore_backend.onkore.Repository.AdminRepository;
import com.onkore_backend.onkore.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PasswordResetServices {

    @Autowired
    private UserRepository userRepository; // Assuming you have a UserRepository for MongoDB

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private JavaMailSender mailSender; // For sending the email

    @Autowired
    private PasswordEncoder passwordEncoder; // For hashing the new password

    public void resetUsersPasswordAction(String email) {
        // Step 1: Find the user by email
        User user = (User) userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Generate a secure token with the current timestamp appended
        String resetToken = generateResetToken();
        System.out.println(resetToken);

        // Step 3: Set the token in the user's record
        user.setResetPasswordToken(resetToken);
        userRepository.save(user);

        System.out.println(user.getResetPasswordToken());

        // Step 4: Send an email with the reset link
        sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    public void resetAdminsPasswordAction(String email) {
        // Step 1: Find the user by email
        Admin admin = adminRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        // Step 2: Generate a secure token with the current timestamp appended
        String resetToken = generateResetToken();

        // Step 3: Set the token in the user's record
        admin.setResetPasswordToken(resetToken);
        adminRepository.save(admin);

        System.out.println(admin.getResetPasswordToken());

        // Step 4: Send an email with the reset link
        sendResetPasswordEmail(admin.getEmail(), resetToken);
    }

    private String generateResetToken() {
        // Generate a secure random token and append the current timestamp (e.g., 15-digit timestamp)
        String randomToken = UUID.randomUUID().toString(); // Random part of the token
        String timestamp = String.valueOf(System.currentTimeMillis()); // Current timestamp
        return randomToken + "-&&-" + timestamp; // Combine them into a single string
    }

    private void sendResetPasswordEmail(String email, String resetToken) {
        String resetUrl = "http://localhost:5173/user/reset-password/" + resetToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("onkore@gmail.com");
        message.setTo(email);
        message.setSubject("Password Reset Request from Onkore");
        message.setText("Click the link below to reset your password:\n" + resetUrl);

        try {
            mailSender.send(message);
            System.out.println("Password reset email sent successfully.");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void resetUsersPassword(String email, String token, String newPassword) {
        // Step 1: Extract the token parts (random token and timestamp)
        String[] tokenParts = token.split("-&&-");
        if (tokenParts.length != 2) {
            System.out.println("Invalid token");
            throw new RuntimeException("Invalid token format");
        }

        String randomToken = tokenParts[0]; // Random part of the token
        long timestamp = Long.parseLong(tokenParts[1]); // Timestamp part of the token

        // Step 2: Find the user with the matching token
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Step 3: Check if the token is expired (within 15 minutes)
        long currentTime = System.currentTimeMillis();
        if (currentTime - timestamp > 900000) { // 900000 ms = 15 minutes
            return;
        }

        if (!token.equals(user.getResetPasswordToken())) {
            System.out.println("Tokens are not the same: " + randomToken + "  " + user.getResetPasswordToken());
            return;
        }

        // Step 4: Hash the new password and update the user's password
        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);
        user.setResetPasswordToken("");

        // Save the updated user to the database
        userRepository.save(user);
    }

    public void resetAdminsPassword(String email, String token, String newPassword) {
        // Step 1: Extract the token parts (random token and timestamp)
        String[] tokenParts = token.split("-&&-");
        if (tokenParts.length != 2) {
            System.out.println("Invalid token");
            throw new RuntimeException("Invalid token format");
        }

        String randomToken = tokenParts[0]; // Random part of the token
        long timestamp = Long.parseLong(tokenParts[1]); // Timestamp part of the token

        // Step 2: Find the user with the matching token
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));

        // Step 3: Check if the token is expired (within 15 minutes)
        long currentTime = System.currentTimeMillis();
        if (currentTime - timestamp > 900000) { // 900000 ms = 15 minutes
            return;
        }

        if (!token.equals(admin.getResetPasswordToken())) {
            System.out.println("Tokens are not the same: " + randomToken + "  " + admin.getResetPasswordToken());
            return;
        }

        // Step 4: Hash the new password and update the user's password
        String hashedPassword = passwordEncoder.encode(newPassword);
        admin.setPassword(hashedPassword);
        admin.setResetPasswordToken("");

        // Save the updated user to the database
        adminRepository.save(admin);
    }
}
