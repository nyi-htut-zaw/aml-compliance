package com.bank.aml.cases.repository;

import com.bank.aml.cases.model.Case;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CaseRepository extends JpaRepository<Case, UUID> {
    Optional<Case> findByCaseNumber(String caseNumber);

    @Query("SELECT c FROM Case c WHERE " +
           "(:status IS NULL OR c.status = :status) AND " +
           "(:priority IS NULL OR c.priority = :priority) AND " +
           "(:customerId IS NULL OR c.customer.id = :customerId)")
    Page<Case> searchCases(
            @Param("status") String status,
            @Param("priority") String priority,
            @Param("customerId") UUID customerId,
            Pageable pageable);

    long countByStatus(String status);
    long countByPriority(String priority);
    long countBySlaDueAtBeforeAndClosedAtIsNull(Instant cutoffTime);
}
