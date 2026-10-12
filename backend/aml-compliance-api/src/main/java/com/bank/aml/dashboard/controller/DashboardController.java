package com.bank.aml.dashboard.controller;

import com.bank.aml.alert.repository.AlertRepository;
import com.bank.aml.alert.service.AlertService;
import com.bank.aml.cases.repository.CaseRepository;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.reporting.repository.RegulatoryReportRepository;
import com.bank.aml.transaction.repository.TransactionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@Tag(name = "Dashboard", description = "Real-time KPI metrics for the executive dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final AlertRepository alertRepository;
    private final CaseRepository caseRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final RegulatoryReportRepository reportRepository;
    private final AlertService alertService;

    public DashboardController(
            AlertRepository alertRepository,
            CaseRepository caseRepository,
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository,
            RegulatoryReportRepository reportRepository,
            AlertService alertService) {
        this.alertRepository = alertRepository;
        this.caseRepository = caseRepository;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.reportRepository = reportRepository;
        this.alertService = alertService;
    }

    @GetMapping("/kpis")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Get live dashboard KPI metrics: alert backlog, case SLA, risk distribution")
    public ResponseEntity<Map<String, Object>> getKpis() {
        Map<String, Object> kpis = new HashMap<>();

        // Alert KPIs
        AlertService.AlertMetricsDto alertMetrics = alertService.getAlertMetrics();
        kpis.put("alertMetrics", alertMetrics);

        // Case KPIs
        Map<String, Object> caseMetrics = new HashMap<>();
        caseMetrics.put("totalActive", caseRepository.countByStatus("IN_INVESTIGATION") +
                caseRepository.countByStatus("ASSIGNED") + caseRepository.countByStatus("NEW"));
        caseMetrics.put("escalatedToMlro", caseRepository.countByStatus("ESCALATED_MLRO"));
        caseMetrics.put("closedSuspicious", caseRepository.countByStatus("CLOSED_SUSPICIOUS"));
        caseMetrics.put("filedStr", caseRepository.countByStatus("FILED_STR"));
        caseMetrics.put("slaBreached", caseRepository.countBySlaDueAtBeforeAndClosedAtIsNull(Instant.now()));
        kpis.put("caseMetrics", caseMetrics);

        // Customer KPIs
        Map<String, Object> customerMetrics = new HashMap<>();
        customerMetrics.put("totalCustomers", customerRepository.count());
        customerMetrics.put("highRisk", customerRepository.countByRiskRating("HIGH"));
        customerMetrics.put("criticalRisk", customerRepository.countByRiskRating("CRITICAL"));
        customerMetrics.put("kycExpired", customerRepository.countByKycStatus("EXPIRED"));
        kpis.put("customerMetrics", customerMetrics);

        // Transaction KPIs
        Map<String, Object> transactionMetrics = new HashMap<>();
        transactionMetrics.put("totalTransactions", transactionRepository.count());
        kpis.put("transactionMetrics", transactionMetrics);

        // Reporting KPIs
        Map<String, Object> reportMetrics = new HashMap<>();
        reportMetrics.put("draftReports", reportRepository.countByStatus("DRAFT"));
        reportMetrics.put("approvedReports", reportRepository.countByStatus("APPROVED"));
        reportMetrics.put("exportedReports", reportRepository.countByStatus("EXPORTED"));
        kpis.put("reportMetrics", reportMetrics);

        kpis.put("generatedAt", Instant.now());

        return ResponseEntity.ok(kpis);
    }
}
