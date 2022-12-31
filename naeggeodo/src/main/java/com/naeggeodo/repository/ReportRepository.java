package com.naeggeodo.repository;

import com.naeggeodo.entity.post.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
