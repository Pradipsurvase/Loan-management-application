package com.example.educationloan.controller;

import com.example.educationloan.security.jwt.AuthService;
import com.example.educationloan.dto.AuthDTO;
import com.example.educationloan.dto.LoginDTO;
import com.example.educationloan.dto.RegisterDTO;
import com.example.educationloan.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthDTO>> register(@Valid @RequestBody RegisterDTO request) {
        AuthDTO response = authService.register(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "User registered successfully", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthDTO>> login(@Valid @RequestBody LoginDTO request) {
        AuthDTO response = authService.login(request.getUsernameOrEmail(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthDTO>> refresh(@RequestParam String refreshToken) {
        AuthDTO response = authService.refresh(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed successfully", response));
    }
}