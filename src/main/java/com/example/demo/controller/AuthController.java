package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.JwtResponse;
import com.example.demo.dto.RegistrationRequest;
import com.example.demo.infra.JwtUtils;
import com.example.demo.infra.WebAuthnService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    private WebAuthnService webAuthnService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/register/start")
    public ResponseEntity<?> startRegistration(@RequestBody RegistrationRequest request) {
        Map<String, Object> options = webAuthnService.startRegistration(
                request.getUsername(),
                request.getEmail(),
                request.getDisplayName()
        );
        return ResponseEntity.ok(options);
    }

    @PostMapping("/register/finish")
    public ResponseEntity<?> finishRegistration(@RequestBody Map<String, Object> request) {
        String challengeId = (String) request.get("challengeId");
        Map<String, Object> credential = (Map<String, Object>) request.get("credential");

        boolean success = webAuthnService.finishRegistration(challengeId, credential);

        if (success) {
            return ResponseEntity.ok(Map.of("success", true, "message", "Registration successful"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Registration failed"));
        }
    }

    @PostMapping("/login/start")
    public ResponseEntity<?> startAuthentication(HttpServletRequest request) {
        Map<String, Object> options = webAuthnService.startAuthentication();
        if (options == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }

        return ResponseEntity.ok(options);
    }

    @PostMapping("/login/finish")
    public ResponseEntity<?> finishAuthentication(@RequestBody Map<String, Object> request) {
        String challengeId = (String) request.get("challengeId");
        Map<String, Object> credential = (Map<String, Object>) request.get("credential");

        User user = webAuthnService.finishAuthentication(challengeId, credential);

        if (user != null) {
            String jwt = jwtUtils.generateJwtToken(user.getUsername());
            return ResponseEntity.ok(new JwtResponse(jwt, user.getUsername(), user.getEmail()));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Authentication failed"));
        }
    }
}