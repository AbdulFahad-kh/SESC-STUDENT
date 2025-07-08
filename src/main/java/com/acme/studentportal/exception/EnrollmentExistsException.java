package com.acme.studentportal.exception;

public class EnrollmentExistsException extends RuntimeException {
    public EnrollmentExistsException(String message) {
        super(message);
    }
}
