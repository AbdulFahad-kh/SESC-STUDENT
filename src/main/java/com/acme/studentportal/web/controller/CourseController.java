package com.acme.studentportal.web.controller;

import com.acme.studentportal.domain.model.Course;
import com.acme.studentportal.service.CourseService;
import com.acme.studentportal.web.request.EnrollCourseRequest;
import com.acme.studentportal.web.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Course>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Course>> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(id)));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<ApiResponse<Course>> getCourseByCode(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseByCode(code)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Course>>> searchCourses(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(courseService.searchCourses(query)));
    }

    @PostMapping("/enroll")
    public ResponseEntity<ApiResponse<?>> enrollInCourse(@Valid @RequestBody EnrollCourseRequest request) {
        courseService.enrollInCourse(request);
        return ResponseEntity.ok(ApiResponse.success("Successfully enrolled in the course"));
    }
}
