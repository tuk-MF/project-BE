package com.example.backend.work;

import com.example.backend.work.Work;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkRepository extends JpaRepository<Work, Long> {
}
