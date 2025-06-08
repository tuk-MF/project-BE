package com.example.backend.ApplicationHistory;

import com.example.backend.ApplicationHistory.ApplicationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationHistoryRepository extends JpaRepository<ApplicationHistory, Long> {
}
