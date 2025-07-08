package com.acme.studentportal.service.impl;

import com.acme.studentportal.domain.model.Course;
import com.acme.studentportal.domain.model.Enrollment;
import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.domain.repository.CourseRepository;
import com.acme.studentportal.domain.repository.EnrollmentRepository;
import com.acme.studentportal.domain.repository.StudentRepository;
import com.acme.studentportal.exception.CourseNotFoundException;
import com.acme.studentportal.exception.EnrollmentExistsException;
import com.acme.studentportal.exception.StudentNotFoundException;
import com.acme.studentportal.service.CourseService;
import com.acme.studentportal.service.FinanceService;
import com.acme.studentportal.service.LibraryService;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.web.request.EnrollCourseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;
    private final FinanceService financeService;
    private final LibraryService libraryService;

    @Override
    public List<Course> getAllCourses() {
        return courseRepository.findByActiveTrue();
    }

    @Override
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with id: " + id));
    }

    @Override
    public Course getCourseByCode(String code) {
        return courseRepository.findByCode(code)
                .orElseThrow(() -> new CourseNotFoundException("Course not found with code: " + code));
    }

    @Override
    public List<Course> searchCourses(String query) {
        return courseRepository.search(query);
    }

    @Override
    @Transactional
    public void enrollInCourse(EnrollCourseRequest request) {
        Student student = userService.getCurrentStudent().orElse(null);
        if (student == null) {
            // Create student profile on first enrolment
            var user = userService.getCurrentUser().orElseThrow(() -> new StudentNotFoundException("User not found"));
            student = new Student(user.getUsername(), user.getUsername()); // Use username as placeholder, update as needed
            student.setUserAccount(user);
            student = studentRepository.save(student);
            user.setStudent(student);
            // Save the userAccount to persist the link
            // (Assume userAccountRepository is available via userService or inject it here)
            if (userService instanceof com.acme.studentportal.service.impl.UserServiceImpl) {
                com.acme.studentportal.service.impl.UserServiceImpl impl = (com.acme.studentportal.service.impl.UserServiceImpl) userService;
                impl.getUserAccountRepository().save(user);
            }
            // Call Finance and Library microservices
            financeService.createStudentAccount(student.getStudentId());
            libraryService.createStudentAccount(student.getStudentId());
        }
        Course course = getCourseById(request.getCourseId());
        if (enrollmentRepository.existsByStudentAndCourseId(student, course.getId())) {
            throw new EnrollmentExistsException("Already enrolled in this course");
        }
        Enrollment enrollment = new Enrollment(student, course);
        enrollmentRepository.save(enrollment);
        // Call Finance to create invoice
        java.time.LocalDate dueDate = java.time.LocalDate.now().plusMonths(1);
        financeService.createInvoice(student.getStudentId(), course.getFee(), "TUITION_FEES", dueDate.toString());
    }
}
