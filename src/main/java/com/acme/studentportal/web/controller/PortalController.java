package com.acme.studentportal.web.controller;

import com.acme.studentportal.domain.model.Course;
import com.acme.studentportal.domain.model.Enrollment;
import com.acme.studentportal.domain.model.Student;
import com.acme.studentportal.domain.repository.CourseRepository;
import com.acme.studentportal.domain.repository.EnrollmentRepository;
import com.acme.studentportal.domain.repository.StudentRepository;
import com.acme.studentportal.service.FinanceService;
import com.acme.studentportal.service.LibraryService;
import com.acme.studentportal.service.UserService;
import com.acme.studentportal.service.CourseService;
import com.acme.studentportal.web.request.RegisterUserRequest;
import com.acme.studentportal.web.request.EnrollCourseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PortalController {
    private final UserService userService;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final FinanceService financeService;
    private final LibraryService libraryService;
    private final PasswordEncoder passwordEncoder;
    private final CourseService courseService;

    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerUserRequest", new RegisterUserRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerSubmit(@ModelAttribute @Valid RegisterUserRequest registerUserRequest, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }
        try {
            userService.registerNewUser(registerUserRequest);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        List<Course> courses = courseService.getAllCourses();
        model.addAttribute("courses", courses);
        return "courses";
    }

    @GetMapping("/courses/{id}")
    public String courseDetails(@PathVariable Long id, Model model) {
        Course course = courseService.getCourseById(id);
        model.addAttribute("course", course);
        return "course";
    }

    @PostMapping("/courses/{id}/enroll")
    public String enrollInCourse(@PathVariable Long id, Principal principal, Model model) {
        EnrollCourseRequest req = new EnrollCourseRequest();
        req.setCourseId(id);
        courseService.enrollInCourse(req);
        return "redirect:/enrollments";
    }

    @GetMapping("/enrollments")
    public String enrollments(Model model) {
        Student student = userService.getCurrentStudent().orElse(null);
        if (student != null) {
            model.addAttribute("enrollments", enrollmentRepository.findByStudent(student));
        } else {
            model.addAttribute("enrollments", java.util.Collections.emptyList());
        }
        return "enrollments";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Student student = userService.getCurrentStudent().orElse(null);
        model.addAttribute("student", student);
        return "profile";
    }

    @GetMapping("/profile/edit")
    public String editProfile(Model model) {
        Student student = userService.getCurrentStudent().orElse(null);
        model.addAttribute("student", student);
        return "profile-edit";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute Student updated, Model model) {
        Student student = userService.getCurrentStudent().orElse(null);
        if (student != null) {
            student.setFirstName(updated.getFirstName());
            student.setLastName(updated.getLastName());
            studentRepository.save(student);
        }
        return "redirect:/profile";
    }

    @GetMapping("/graduation")
    public String graduation(Model model) {
        Student student = userService.getCurrentStudent().orElse(null);
        boolean eligible = false;
        if (student != null) {
            eligible = financeService.isEligibleForGraduation(student.getStudentId());
        }
        model.addAttribute("eligible", eligible);
        return "graduation";
    }

    @GetMapping("/book/{isbn}")
    public String bookDetails(@PathVariable String isbn, Model model) {
        var book = libraryService.getBookByIsbn(isbn);
        model.addAttribute("book", book);
        return "book";
    }

    @GetMapping("/lend-book")
    public String lendBookForm(Model model) {
        var books = libraryService.getAllBooks();
        model.addAttribute("books", books);
        var student = userService.getCurrentStudent().orElse(null);
        List<LibraryService.Book> lendedBooks = List.of();
        if (student != null) {
            lendedBooks = libraryService.getLendedBooks(student.getStudentId());
        }
        model.addAttribute("lendedBooks", lendedBooks);
        return "lend";
    }

    @PostMapping("/lend-book")
    public String lendBookSubmit(@RequestParam String isbn, Model model) {
        var student = userService.getCurrentStudent().orElse(null);
        if (student == null) {
            model.addAttribute("error", "Student profile not found");
            return lendBookForm(model);
        }
        LibraryService.ApiResponse resp = libraryService.lendBook(student.getStudentId(), isbn);
        if (resp.error != null) {
            if (resp.error.toLowerCase().contains("no copies")) {
                model.addAttribute("error", "No copies left for this book.");
            } else {
                model.addAttribute("error", resp.error);
            }
        } else {
            model.addAttribute("message", resp.message);
            if (resp.lendDate != null) model.addAttribute("lendDate", resp.lendDate);
            if (resp.returnDeadline != null) model.addAttribute("returnDeadline", resp.returnDeadline);
            if (resp.lendFee != null) model.addAttribute("lendFee", resp.lendFee);
        }
        return lendBookForm(model);
    }

    @GetMapping("/return-book")
    public String returnBookForm() {
        return "return";
    }

    @PostMapping("/return-book")
    public String returnBookSubmit(@RequestParam String isbn, Model model) {
        var student = userService.getCurrentStudent().orElse(null);
        if (student == null) {
            model.addAttribute("error", "Student profile not found");
            return "return";
        }
        LibraryService.ApiResponse resp = libraryService.returnBook(student.getStudentId(), isbn);
        if (resp.error != null) {
            model.addAttribute("error", resp.error);
        } else {
            model.addAttribute("message", resp.message);
            if (resp.fineIssued) {
                model.addAttribute("fineAmount", resp.fineAmount);
                model.addAttribute("fineReference", resp.fineReference);
            }
        }
        return "return";
    }

    @GetMapping("/outstanding-invoices")
    public String outstandingInvoices(Model model) {
        var student = userService.getCurrentStudent().orElse(null);
        if (student == null) {
            model.addAttribute("invoices", List.of());
            return "outstanding-invoices";
        }
        System.out.println("[DEBUG] PortalController: studentId = " + student.getStudentId());
        var invoices = financeService.getOutstandingInvoices(student.getStudentId());
        model.addAttribute("invoices", invoices);
        return "outstanding-invoices";
    }

    @GetMapping("/fines")
    public String fines(Model model) {
        var student = userService.getCurrentStudent().orElse(null);
        if (student == null) {
            model.addAttribute("fines", List.of());
            return "fines";
        }
        var fines = financeService.getInvoicesByType(student.getStudentId(), "LIBRARY_FINE");
        model.addAttribute("fines", fines);
        return "fines";
    }
} 