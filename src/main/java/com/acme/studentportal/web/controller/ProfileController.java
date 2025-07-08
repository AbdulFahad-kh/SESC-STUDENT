package com.acme.studentportal.web.controller;

import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Student>> getProfile() {
        Student student = userService.getCurrentStudent().orElseThrow(() -> new RuntimeException("Student profile not found"));
        return ResponseEntity.ok(ApiResponse.success(student));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<Student>> updateProfile(@Valid @RequestBody Student updated) {
        Student student = userService.getCurrentStudent().orElseThrow(() -> new RuntimeException("Student profile not found"));
        student.setFirstName(updated.getFirstName());
        student.setLastName(updated.getLastName());
        // Save logic here (assume userService or studentRepository)
        // For now, just return updated
        return ResponseEntity.ok(ApiResponse.success(student));
    }
} 