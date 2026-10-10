package com.bank.aml.reporting.service;

import com.bank.aml.audit.service.AuditService;
import java.util.List;
import com.bank.aml.cases.model.Case;
import com.bank.aml.cases.repository.CaseRepository;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.iam.repository.UserRepository;
import com.bank.aml.reporting.dto.ReportCreateDto;
import com.bank.aml.reporting.dto.ReportDto;
import com.bank.aml.reporting.model.RegulatoryReport;
import com.bank.aml.reporting.repository.RegulatoryReportRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RegulatoryReportingService {

    private final RegulatoryReportRepository reportRepository;
    private final CaseRepository caseRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public RegulatoryReportingService(
            RegulatoryReportRepository reportRepository,
            CaseRepository caseRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.reportRepository = reportRepository;
        this.caseRepository = caseRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ReportDto createReport(ReportCreateDto dto) {
        Case sourceCase = caseRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", dto.getCaseId().toString()));

        if (!List.of("CLOSED_SUSPICIOUS", "FILED_STR", "ESCALATED_MLRO").contains(sourceCase.getStatus())) {
            throw new BusinessRuleException("Regulatory reports can only be filed for cases with suspicious closure or escalated to MLRO. Case status: " + sourceCase.getStatus());
        }

        RegulatoryReport report = new RegulatoryReport();
        report.setReportNumber(dto.getReportType() + "-" + System.currentTimeMillis() % 1_000_000 + "-" + ThreadLocalRandom.current().nextInt(1000, 9999));
        report.setReportType(dto.getReportType());
        report.setSourceCase(sourceCase);
        report.setJurisdictionCountry(dto.getJurisdictionCountry());
        report.setReportingInstitution("DEMO AML COMPLIANCE BANK — NOT A REAL REGULATORY SUBMISSION");
        report.setSubjectName(sourceCase.getCustomer().getFullName());
        report.setNarrative(dto.getNarrative());
        report.setTotalAmountUsd(dto.getTotalAmountUsd());
        report.setStatus("DRAFT");

        // Generate goAML-compatible JSON template
        report.setReportJson(generateGoAmlJson(report, sourceCase));

        SecurityUtils.getCurrentUserId().flatMap(userRepository::findById)
                .ifPresent(report::setPreparedBy);

        RegulatoryReport saved = reportRepository.save(report);

        // Update case status to FILED_STR
        sourceCase.setStatus("FILED_STR");
        caseRepository.save(sourceCase);

        auditService.recordEvent("REPORTING", "CREATE_REPORT", "REGULATORY_REPORT", saved.getId().toString(),
                dto.getReportType() + " report " + saved.getReportNumber() + " created for case " + sourceCase.getCaseNumber());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ReportDto> searchReports(String status, String reportType, Pageable pageable) {
        return reportRepository.searchReports(status, reportType, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public ReportDto getReportById(UUID id) {
        return reportRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("RegulatoryReport", "id", id.toString()));
    }

    @Transactional
    public ReportDto approveReport(UUID id) {
        RegulatoryReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegulatoryReport", "id", id.toString()));

        if (!"DRAFT".equals(report.getStatus()) && !"UNDER_REVIEW".equals(report.getStatus())) {
            throw new BusinessRuleException("Report " + report.getReportNumber() + " cannot be approved in status: " + report.getStatus());
        }

        report.setStatus("APPROVED");
        report.setUpdatedAt(Instant.now());

        SecurityUtils.getCurrentUserId().flatMap(userRepository::findById)
                .ifPresent(report::setApprovedBy);

        RegulatoryReport saved = reportRepository.save(report);

        auditService.recordEvent("REPORTING", "APPROVE_REPORT", "REGULATORY_REPORT", id.toString(),
                "Report " + report.getReportNumber() + " approved for submission");

        return mapToDto(saved);
    }

    @Transactional
    public ReportDto exportReport(UUID id) {
        RegulatoryReport report = reportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("RegulatoryReport", "id", id.toString()));

        if (!"APPROVED".equals(report.getStatus())) {
            throw new BusinessRuleException("Only APPROVED reports can be exported. Current status: " + report.getStatus());
        }

        report.setStatus("EXPORTED");
        report.setSubmittedAt(Instant.now());
        report.setUpdatedAt(Instant.now());
        RegulatoryReport saved = reportRepository.save(report);

        auditService.recordEvent("REPORTING", "EXPORT_REPORT", "REGULATORY_REPORT", id.toString(),
                "Report " + report.getReportNumber() + " marked as EXPORTED (demo — no real submission)");

        return mapToDto(saved);
    }

    private String generateGoAmlJson(RegulatoryReport report, Case sourceCase) {
        // DEMO: goAML-compatible JSON structure. NOT a real submission format.
        return String.format("""
        {
          "_disclaimer": "DEMO PLATFORM — NOT A REAL REGULATORY SUBMISSION",
          "report_type": "%s",
          "report_number": "%s",
          "jurisdiction": "%s",
          "reporting_entity": {
            "name": "%s",
            "type": "FINANCIAL_INSTITUTION"
          },
          "subject": {
            "name": "%s",
            "customer_id": "%s"
          },
          "suspicious_activity": {
            "case_reference": "%s",
            "narrative": "%s",
            "total_amount_usd": %.2f
          },
          "created_at": "%s"
        }
        """,
                report.getReportType(),
                report.getReportNumber(),
                report.getJurisdictionCountry(),
                report.getReportingInstitution(),
                report.getSubjectName(),
                sourceCase.getCustomer().getId(),
                sourceCase.getCaseNumber(),
                report.getNarrative().replace("\"", "'"),
                report.getTotalAmountUsd(),
                Instant.now()
        );
    }

    private ReportDto mapToDto(RegulatoryReport r) {
        ReportDto dto = new ReportDto();
        dto.setId(r.getId());
        dto.setReportNumber(r.getReportNumber());
        dto.setReportType(r.getReportType());
        dto.setCaseId(r.getSourceCase().getId());
        dto.setCaseNumber(r.getSourceCase().getCaseNumber());
        dto.setStatus(r.getStatus());
        dto.setJurisdictionCountry(r.getJurisdictionCountry());
        dto.setReportingInstitution(r.getReportingInstitution());
        dto.setSubjectName(r.getSubjectName());
        dto.setNarrative(r.getNarrative());
        dto.setTotalAmountUsd(r.getTotalAmountUsd());
        dto.setReportJson(r.getReportJson());
        if (r.getPreparedBy() != null) dto.setPreparedByUsername(r.getPreparedBy().getUsername());
        if (r.getApprovedBy() != null) dto.setApprovedByUsername(r.getApprovedBy().getUsername());
        dto.setSubmittedAt(r.getSubmittedAt());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        return dto;
    }
}
