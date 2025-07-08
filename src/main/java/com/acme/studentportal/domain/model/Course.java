package com.acme.studentportal.domain.model;

import lombok.Data;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Data
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String code;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    private int creditHours;
    private boolean active = true;
    private double fee;
    
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Enrollment> enrollments = new HashSet<>();
    
    public Course() {}
    
    public Course(String code, String title, String description, int creditHours) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.creditHours = creditHours;
    }
}
