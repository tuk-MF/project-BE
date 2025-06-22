package com.example.backend.work;

import com.example.backend.user.entity.User;
import com.example.backend.work.dto.WorkRequest;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "works")
public class Work {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String phone;
    private String title;
    private Integer hourlyWage;
    private String payType;
    private String category;
    private String address;
    private String detailAddress;
    private String description;
    private String imageUrl;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, updatable = false)
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

    public void updateFromRequest(WorkRequest request) {
        this.title = request.getTitle();
        this.hourlyWage = request.getHourlyWage();
        this.payType = request.getPayType();
        this.category = request.getCategory();
        this.address = request.getAddress();
        this.detailAddress = request.getDetailAddress();
        this.description = request.getDescription();
    }

}