package com.bank.aml.screening.repository;

import com.bank.aml.screening.model.ScreeningMatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ScreeningMatchRepository extends JpaRepository<ScreeningMatch, UUID> {
    List<ScreeningMatch> findByCustomerIdOrderByMatchScoreDesc(UUID customerId);
    Page<ScreeningMatch> findByDisposition(String disposition, Pageable pageable);
    long countByDisposition(String disposition);
}
