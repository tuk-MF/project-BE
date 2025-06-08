package com.example.backend.companyjob;

import com.example.backend.companyjob.CompanyJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyJobRepository extends JpaRepository<CompanyJob, Long> {
}
