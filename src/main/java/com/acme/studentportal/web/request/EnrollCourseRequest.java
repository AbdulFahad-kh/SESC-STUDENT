package com.acme.studentportal.web.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class EnrollCourseRequest {
    @NotNull(message = "Course ID is required")
    private Long courseId;
}
