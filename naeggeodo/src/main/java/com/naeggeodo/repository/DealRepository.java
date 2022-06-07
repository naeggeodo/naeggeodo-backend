package com.naeggeodo.repository;

import com.naeggeodo.entity.deal.Deal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DealRepository extends JpaRepository<Deal,Long> {
}
