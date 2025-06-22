package com.example.backend.ApplicationHistory.repository;

import com.example.backend.ApplicationHistory.entity.ApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistory, Long> {
    boolean existsByUserIdAndWorkId(Long userId, Long workId);
    List<ApplicationHistory> findByWorkId(Long workId);
}
