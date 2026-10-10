package com.bank.aml.alert.repository;

import com.bank.aml.alert.model.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    Optional<Alert> findByAlertNumber(String alertNumber);

    @Query("SELECT a FROM Alert a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:severity IS NULL OR a.severity = :severity) AND " +
           "(:customerId IS NULL OR a.customer.id = :customerId)")
    Page<Alert> searchAlerts(
            @Param("status") String status,
            @Param("severity") String severity,
            @Param("customerId") UUID customerId,
            Pageable pageable);

    long countByStatus(String status);
    long countBySeverity(String severity);
    long countBySlaDueAtBeforeAndClosedAtIsNull(Instant cutoffTime);

    List<Alert> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
