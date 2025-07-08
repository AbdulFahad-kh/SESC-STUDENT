package com.acme.studentportal.web.controller;

import com.acme.studentportal.domain.model.Enrollment;
import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Enrollment>>> getCurrentStudentEnrollments() {
        Student student = userService.getCurrentStudent().orElseThrow(() -> new RuntimeException("Student profile not found"));
        return ResponseEntity.ok(ApiResponse.success(student.getEnrollments()));
    }
} 