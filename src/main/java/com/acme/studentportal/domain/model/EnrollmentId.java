package com.acme.studentportal.domain.model;

import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

@Embeddable
@Data
public class EnrollmentId implements Serializable {
    
    @Column(name = "student_id")
    private Long studentId;
    
    @Column(name = "course_id")
    private Long courseId;
    
    public EnrollmentId() {}
    
    public EnrollmentId(Long studentId, Long courseId) {
        this.studentId = studentId;
        this.courseId = courseId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EnrollmentId that = (EnrollmentId) o;
        return studentId.equals(that.studentId) && courseId.equals(that.courseId);
    }
    
    @Override
    public int hashCode() {
        return 31 * studentId.hashCode() + courseId.hashCode();
    }
}
