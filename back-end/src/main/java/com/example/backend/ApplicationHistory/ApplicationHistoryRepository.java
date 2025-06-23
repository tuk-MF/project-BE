package com.example.backend.ApplicationHistory;

import com.example.backend.ApplicationHistory.ApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistory, Long> {
    boolean existsByUserIdAndWorkId(Long userId, Long workId);
    List<ApplicationHistory> findByWorkId(Long workId);
    List<ApplicationHistory> findByUserId(Long userId);
}
