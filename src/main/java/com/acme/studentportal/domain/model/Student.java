package com.acme.studentportal.domain.model;

import lombok.Data;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "students")
@Data
public class Student {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String studentId;
    
    @Column(nullable = false)
    private String firstName;
    
    @Column(nullable = false)
    private String lastName;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
    
    @OneToOne(mappedBy = "student")
    private UserAccount userAccount;
    
    public Student() {}
    
    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
        generateStudentId();
    }
    
    private void generateStudentId() {
        if (this.studentId == null) {
            this.studentId = "S" + 
                (int)(Math.random() * 9) + 
                String.format("%06d", (int)(Math.random() * 1000000));
        }
    }
}
