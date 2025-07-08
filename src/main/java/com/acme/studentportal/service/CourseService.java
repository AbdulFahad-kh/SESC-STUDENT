package com.acme.studentportal.service;

import com.acme.studentportal.domain.model.Course;
import com.acme.studentportal.web.request.EnrollCourseRequest;

import java.util.List;

public interface CourseService {
    List<Course> getAllCourses();
    Course getCourseById(Long id);
    Course getCourseByCode(String code);
    List<Course> searchCourses(String query);
    void enrollInCourse(EnrollCourseRequest request);
}
