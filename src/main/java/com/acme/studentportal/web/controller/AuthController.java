package com.acme.studentportal.web.controller;

import com.acme.studentportal.domain.model.UserAccount;
import com.acme.studentportal.security.JwtTokenProvider;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.request.LoginRequest;
import com.acme.studentportal.web.request.RegisterUserRequest;
import com.acme.studentportal.web.response.ApiResponse;
import com.acme.studentportal.web.response.JwtAuthenticationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        userService.registerNewUser(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserAccount>> getCurrentUser() {
        return userService.getCurrentUser()
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElseGet(() -> ResponseEntity.ok(ApiResponse.success(null)));
    }
}
