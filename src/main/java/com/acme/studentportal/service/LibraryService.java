package com.acme.studentportal.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class LibraryService {
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${app.library-base-url:http://localhost:5000}")
    private String libraryBaseUrl;

    public void createStudentAccount(String studentId) {
        restTemplate.postForObject(libraryBaseUrl + "/api/register", new Account(studentId), Void.class);
    }

    public ApiResponse returnBook(String studentId, String isbn) {
        LendReturnRequest req = new LendReturnRequest(studentId, isbn);
        try {
            return restTemplate.postForObject(libraryBaseUrl + "/api/return_book", req, ApiResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            ApiResponse resp = new ApiResponse();
            resp.error = "Book not found";
            return resp;
        }
    }

    public Book getBookByIsbn(String isbn) {
        try {
            return restTemplate.getForObject(libraryBaseUrl + "/api/book/" + isbn, Book.class);
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }

    public ApiResponse lendBook(String studentId, String isbn) {
        LendReturnRequest req = new LendReturnRequest(studentId, isbn);
        try {
            return restTemplate.postForObject(libraryBaseUrl + "/api/lend_book", req, ApiResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            ApiResponse resp = new ApiResponse();
            resp.error = "Book not found";
            return resp;
        }
    }

    public List<Book> getAllBooks() {
        try {
            Book[] books = restTemplate.getForObject(libraryBaseUrl + "/api/books", Book[].class);
            return books != null ? Arrays.asList(books) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    public List<Book> getLendedBooks(String studentId) {
        try {
            Book[] books = restTemplate.getForObject(libraryBaseUrl + "/api/borrowed_books/" + studentId, Book[].class);
            return books != null ? Arrays.asList(books) : Collections.emptyList();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    static class Account {
        public String studentId;
        public Account(String studentId) { this.studentId = studentId; }
    }

    static class ReturnRequest {
        public String studentId;
        public String isbn;
        public ReturnRequest(String studentId, String isbn) {
            this.studentId = studentId;
            this.isbn = isbn;
        }
    }

    static class LendReturnRequest {
        public String studentId;
        public String isbn;
        public LendReturnRequest(String studentId, String isbn) {
            this.studentId = studentId;
            this.isbn = isbn;
        }
    }

    public static class Book {
        public String isbn;
        public String title;
        public String author;
        public String year;
        public int copies;
    }

    public static class ApiResponse {
        public String message;
        public boolean fineIssued;
        public double fineAmount;
        public String fineReference;
        public String error;
        public String lendDate;
        public String returnDeadline;
        public Double lendFee;
    }
} 