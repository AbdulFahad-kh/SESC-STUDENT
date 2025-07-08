package com.acme.studentportal.domain.repository;

import com.acme.studentportal.domain.model.Enrollment;
import com.acme.studentportal.domain.model.EnrollmentId;
import com.acme.studentportal.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, EnrollmentId> {
    List<Enrollment> findByStudentId(Long studentId);
    boolean existsByStudentAndCourseId(Student student, Long courseId);
    int countByStudentId(Long studentId);
    List<Enrollment> findByStudent(Student student);
}
