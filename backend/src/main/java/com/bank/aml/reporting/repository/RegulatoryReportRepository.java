package com.bank.aml.reporting.repository;

import com.bank.aml.reporting.model.RegulatoryReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RegulatoryReportRepository extends JpaRepository<RegulatoryReport, UUID> {
    Optional<RegulatoryReport> findByReportNumber(String reportNumber);

    @Query("SELECT r FROM RegulatoryReport r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:reportType IS NULL OR r.reportType = :reportType)")
    Page<RegulatoryReport> searchReports(
            @Param("status") String status,
            @Param("reportType") String reportType,
            Pageable pageable);

    long countByStatus(String status);
    long countByReportType(String reportType);
}
