package com.bank.aml.alert.service;

import com.bank.aml.alert.dto.AlertDispositionDto;
import com.bank.aml.alert.dto.AlertDto;
import com.bank.aml.alert.model.Alert;
import com.bank.aml.alert.repository.AlertRepository;
import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.iam.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public AlertService(AlertRepository alertRepository, UserRepository userRepository, AuditService auditService) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public Page<AlertDto> searchAlerts(String status, String severity, UUID customerId, Pageable pageable) {
        return alertRepository.searchAlerts(status, severity, customerId, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public AlertDto getAlertById(UUID id) {
        return alertRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id.toString()));
    }

    @Transactional(readOnly = true)
    public AlertDto getAlertByNumber(String alertNumber) {
        return alertRepository.findByAlertNumber(alertNumber)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "alertNumber", alertNumber));
    }

    @Transactional
    public AlertDto assignAlert(UUID alertId, UUID assignToUserId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", alertId.toString()));

        if (List.of("CLOSED_FALSE_POSITIVE", "CLOSED_TRUE_POSITIVE", "CASE_CREATED").contains(alert.getStatus())) {
            throw new BusinessRuleException("Cannot assign a closed alert: " + alert.getAlertNumber());
        }

        var user = userRepository.findById(assignToUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", assignToUserId.toString()));

        alert.setAssignedToUser(user);
        alert.setStatus("ASSIGNED");
        alert.setUpdatedAt(Instant.now());
        Alert saved = alertRepository.save(alert);

        auditService.recordEvent("ALERTS", "ASSIGN_ALERT", "ALERT", alertId.toString(),
                "Alert " + alert.getAlertNumber() + " assigned to " + user.getUsername());

        return mapToDto(saved);
    }

    @Transactional
    public AlertDto disposeAlert(UUID alertId, AlertDispositionDto dto) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", alertId.toString()));

        if (List.of("CLOSED_FALSE_POSITIVE", "CLOSED_TRUE_POSITIVE", "CASE_CREATED").contains(alert.getStatus())) {
            throw new BusinessRuleException("Alert " + alert.getAlertNumber() + " is already closed.");
        }

        // Map disposition to a status value
        String newStatus = switch (dto.getDisposition()) {
            case "CLOSED_FALSE_POSITIVE" -> "CLOSED_FALSE_POSITIVE";
            case "CLOSED_TRUE_POSITIVE"  -> "CLOSED_TRUE_POSITIVE";
            case "ESCALATED"             -> "ESCALATED";
            case "CONVERT_TO_CASE"       -> "CASE_CREATED";
            default -> throw new BusinessRuleException("Invalid disposition: " + dto.getDisposition());
        };

        String prevStatus = alert.getStatus();
        alert.setStatus(newStatus);
        alert.setDispositionNotes(dto.getNotes());
        alert.setUpdatedAt(Instant.now());

        if (newStatus.startsWith("CLOSED")) {
            alert.setClosedAt(Instant.now());
        }

        Alert saved = alertRepository.save(alert);

        auditService.recordEvent("ALERTS", "DISPOSE_ALERT", "ALERT", alertId.toString(),
                String.format("Alert %s status changed from %s to %s. Notes: %s",
                        alert.getAlertNumber(), prevStatus, newStatus, dto.getNotes()));

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public AlertMetricsDto getAlertMetrics() {
        AlertMetricsDto metrics = new AlertMetricsDto();
        metrics.setTotalNew(alertRepository.countByStatus("NEW"));
        metrics.setTotalAssigned(alertRepository.countByStatus("ASSIGNED"));
        metrics.setTotalUnderReview(alertRepository.countByStatus("UNDER_REVIEW"));
        metrics.setTotalEscalated(alertRepository.countByStatus("ESCALATED"));
        metrics.setOverdueSla(alertRepository.countBySlaDueAtBeforeAndClosedAtIsNull(Instant.now()));
        metrics.setCritical(alertRepository.countBySeverity("CRITICAL"));
        metrics.setHigh(alertRepository.countBySeverity("HIGH"));
        metrics.setMedium(alertRepository.countBySeverity("MEDIUM"));
        metrics.setLow(alertRepository.countBySeverity("LOW"));
        return metrics;
    }

    public AlertDto mapToDto(Alert alert) {
        AlertDto dto = new AlertDto();
        dto.setId(alert.getId());
        dto.setAlertNumber(alert.getAlertNumber());
        dto.setTitle(alert.getTitle());
        dto.setCustomerId(alert.getCustomer().getId());
        dto.setCustomerName(alert.getCustomer().getFullName());
        if (alert.getAccount() != null) dto.setAccountId(alert.getAccount().getId());
        if (alert.getTransaction() != null) dto.setTransactionId(alert.getTransaction().getId());
        if (alert.getRule() != null) {
            dto.setRuleCode(alert.getRule().getRuleCode());
            dto.setRuleName(alert.getRule().getName());
        }
        dto.setTriggerEvent(alert.getTriggerEvent());
        dto.setSeverity(alert.getSeverity());
        dto.setScore(alert.getScore());
        dto.setStatus(alert.getStatus());
        if (alert.getAssignedToUser() != null) {
            dto.setAssignedToUserId(alert.getAssignedToUser().getId());
            dto.setAssignedToUsername(alert.getAssignedToUser().getUsername());
        }
        dto.setSlaDueAt(alert.getSlaDueAt());
        dto.setTriggerPayloadJson(alert.getTriggerPayloadJson());
        dto.setDispositionNotes(alert.getDispositionNotes());
        dto.setClosedAt(alert.getClosedAt());
        dto.setCreatedAt(alert.getCreatedAt());
        dto.setUpdatedAt(alert.getUpdatedAt());
        return dto;
    }

    // Inner metrics DTO
    public static class AlertMetricsDto {
        private long totalNew;
        private long totalAssigned;
        private long totalUnderReview;
        private long totalEscalated;
        private long overdueSla;
        private long critical;
        private long high;
        private long medium;
        private long low;

        public long getTotalNew() { return totalNew; }
        public void setTotalNew(long v) { this.totalNew = v; }
        public long getTotalAssigned() { return totalAssigned; }
        public void setTotalAssigned(long v) { this.totalAssigned = v; }
        public long getTotalUnderReview() { return totalUnderReview; }
        public void setTotalUnderReview(long v) { this.totalUnderReview = v; }
        public long getTotalEscalated() { return totalEscalated; }
        public void setTotalEscalated(long v) { this.totalEscalated = v; }
        public long getOverdueSla() { return overdueSla; }
        public void setOverdueSla(long v) { this.overdueSla = v; }
        public long getCritical() { return critical; }
        public void setCritical(long v) { this.critical = v; }
        public long getHigh() { return high; }
        public void setHigh(long v) { this.high = v; }
        public long getMedium() { return medium; }
        public void setMedium(long v) { this.medium = v; }
        public long getLow() { return low; }
        public void setLow(long v) { this.low = v; }
    }
}
