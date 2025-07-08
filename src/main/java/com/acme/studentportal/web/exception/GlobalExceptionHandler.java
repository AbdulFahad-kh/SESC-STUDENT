package com.acme.studentportal.web.exception;

import com.acme.studentportal.exception.CourseNotFoundException;
import com.acme.studentportal.exception.EnrollmentExistsException;
import com.acme.studentportal.exception.StudentNotFoundException;
import com.acme.studentportal.exception.UsernameAlreadyExistsException;
import com.acme.studentportal.web.response.ApiResponse;
import com.acme.studentportal.web.response.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage(),
            request.getDescription(false),
            null
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public final ResponseEntity<ApiResponse<?>> handleUsernameAlreadyExists(UsernameAlreadyExistsException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(CourseNotFoundException.class)
    public final ResponseEntity<ApiResponse<?>> handleCourseNotFound(CourseNotFoundException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public final ResponseEntity<ApiResponse<?>> handleStudentNotFound(StudentNotFoundException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(ex.getMessage()),
            HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(EnrollmentExistsException.class)
    public final ResponseEntity<ApiResponse<?>> handleEnrollmentExists(EnrollmentExistsException ex) {
        return new ResponseEntity<>(
            ApiResponse.error(ex.getMessage()),
            HttpStatus.CONFLICT
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers,
            org.springframework.http.HttpStatus status, WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ErrorResponse errorResponse = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Validation error. Check 'errors' field for details.",
            request.getDescription(false),
            errors.entrySet().stream()
                .map(entry -> new ErrorResponse.FieldError(
                    entry.getKey(),
                    entry.getValue(),
                    null
                ))
                .collect(Collectors.toList())
        );

        return handleExceptionInternal(ex, errorResponse, headers, HttpStatus.BAD_REQUEST, request);
    }
}
