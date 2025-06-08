package com.example.backend.ApplicationHistory;


import com.example.backend.user.entity.User;
import com.example.backend.work.Work;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "application_histories")
public class ApplicationHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "work_id")
    private Work work;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private LocalDateTime appliedAt;

    @PrePersist
    public void prePersist() {
        this.appliedAt = LocalDateTime.now();
    }
}
