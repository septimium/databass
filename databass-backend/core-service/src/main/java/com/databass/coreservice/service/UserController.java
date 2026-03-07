package com.databass.coreservice.service;

import com.databass.coreservice.model.User;
import com.databass.coreservice.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "credits", user.getCredits(),
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""
        ));
    }

    @GetMapping("/public/{username}")
    public ResponseEntity<?> getPublicProfile(@PathVariable("username") String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Producer not found"));

        return ResponseEntity.ok(Map.of(
                "username", user.getUsername(),
                "bio", user.getBio() != null ? user.getBio() : "",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""
        ));
    }

    @GetMapping("/settings")
    public ResponseEntity<?> getSettings() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail() != null ? user.getEmail() : "",
                "bio", user.getBio() != null ? user.getBio() : "",
                "avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : ""
        ));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setBio(body.get("bio"));
        user.setAvatarUrl(body.get("avatarUrl"));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Profile updated successfully."));
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();
        user.setEmail(body.get("email"));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Email updated successfully."));
    }

    @PutMapping("/password")
    public ResponseEntity<?> updatePassword(@RequestBody Map<String, String> body) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        if (!passwordEncoder.matches(body.get("current"), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("error", "INCORRECT CURRENT PASSWORD."));
        }

        user.setPassword(passwordEncoder.encode(body.get("new")));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "Password updated successfully."));
    }
}