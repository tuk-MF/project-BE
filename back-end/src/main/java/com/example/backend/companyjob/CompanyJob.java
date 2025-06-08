package com.example.backend.companyjob;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "company_jobs")
public class CompanyJob {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String location;
    private String phone;
    private String content;
    private String jobType;
    private Integer capacity;
    private String organization;

    private String imageUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
