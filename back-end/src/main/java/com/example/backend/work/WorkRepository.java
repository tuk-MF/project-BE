package com.example.backend.work;

import com.example.backend.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkRepository extends JpaRepository<Work, Long> {
    List<Work> findAllByOrderByCreatedAtDesc();
    List<Work> findByUserId(Long userId);

}
