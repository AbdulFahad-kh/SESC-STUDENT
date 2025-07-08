package com.acme.studentportal.domain.repository;

import com.acme.studentportal.domain.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    boolean existsByStudentId(String studentId);
    Optional<Student> findByUserAccountId(Long userAccountId);
    @EntityGraph(attributePaths = "enrollments")
    Optional<Student> findWithEnrollmentsByUserAccountId(Long userAccountId);
}
