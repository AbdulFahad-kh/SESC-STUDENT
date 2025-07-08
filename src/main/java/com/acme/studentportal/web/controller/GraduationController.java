package com.acme.studentportal.web.controller;

import com.acme.studentportal.service.FinanceService;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/graduation")
@RequiredArgsConstructor
public class GraduationController {
    private final UserService userService;
    private final FinanceService financeService;

    @GetMapping
    public ResponseEntity<ApiResponse<Boolean>> checkGraduationEligibility() {
        var student = userService.getCurrentStudent().orElseThrow(() -> new RuntimeException("Student profile not found"));
        boolean eligible = financeService.isEligibleForGraduation(student.getStudentId());
        return ResponseEntity.ok(ApiResponse.success(eligible));
    }
} 