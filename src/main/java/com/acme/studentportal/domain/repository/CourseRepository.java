package com.acme.studentportal.domain.repository;

import com.acme.studentportal.domain.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByCode(String code);
    List<Course> findByActiveTrue();
    boolean existsByCode(String code);
    
    @Query("SELECT c FROM Course c WHERE LOWER(c.title) LIKE LOWER(concat('%', :query, '%')) " +
           "OR LOWER(c.code) LIKE LOWER(concat('%', :query, '%'))")
    List<Course> search(@Param("query") String query);
}
