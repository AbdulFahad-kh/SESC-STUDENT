package com.acme.studentportal.domain.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Data
public class Enrollment {
    
    @EmbeddedId
    private EnrollmentId id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    private Student student;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("courseId")
    private Course course;
    
    private LocalDate enrollmentDate;
    private String status;
    
    public Enrollment() {}
    
    public Enrollment(Student student, Course course) {
        this.student = student;
        this.course = course;
        this.id = new EnrollmentId(student.getId(), course.getId());
        this.enrollmentDate = LocalDate.now();
        this.status = "ACTIVE";
    }
}
