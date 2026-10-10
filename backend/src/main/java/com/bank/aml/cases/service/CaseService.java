package com.bank.aml.cases.service;

import com.bank.aml.alert.model.Alert;
import com.bank.aml.alert.repository.AlertRepository;
import com.bank.aml.audit.service.AuditService;
import com.bank.aml.cases.dto.CaseClosureDto;
import com.bank.aml.cases.dto.CaseCreateDto;
import com.bank.aml.cases.dto.CaseDto;
import com.bank.aml.cases.dto.CaseNoteDto;
import com.bank.aml.cases.model.Case;
import com.bank.aml.cases.model.CaseNote;
import com.bank.aml.cases.repository.CaseNoteRepository;
import com.bank.aml.cases.repository.CaseRepository;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.iam.repository.UserRepository;
import com.bank.aml.transaction.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseNoteRepository caseNoteRepository;
    private final CustomerRepository customerRepository;
    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public CaseService(
            CaseRepository caseRepository,
            CaseNoteRepository caseNoteRepository,
            CustomerRepository customerRepository,
            AlertRepository alertRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.caseRepository = caseRepository;
        this.caseNoteRepository = caseNoteRepository;
        this.customerRepository = customerRepository;
        this.alertRepository = alertRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CaseDto createCase(CaseCreateDto dto) {
        var customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", dto.getCustomerId().toString()));

        Case investigationCase = new Case();
        investigationCase.setCaseNumber("CASE-" + System.currentTimeMillis() % 1_000_000 + "-" + ThreadLocalRandom.current().nextInt(100, 999));
        investigationCase.setTitle(dto.getTitle());
        investigationCase.setDescription(dto.getDescription());
        investigationCase.setPriority(dto.getPriority());
        investigationCase.setStatus("NEW");
        investigationCase.setCustomer(customer);
        investigationCase.setSlaDueAt(Instant.now().plus(72, ChronoUnit.HOURS));

        // Link alerts
        if (dto.getAlertIds() != null && !dto.getAlertIds().isEmpty()) {
            List<Alert> alerts = new ArrayList<>();
            for (UUID alertId : dto.getAlertIds()) {
                alertRepository.findById(alertId).ifPresent(a -> {
                    alerts.add(a);
                    a.setStatus("CASE_CREATED");
                    alertRepository.save(a);
                });
            }
            investigationCase.setLinkedAlerts(alerts);
        }

        // Link transactions
        if (dto.getTransactionIds() != null && !dto.getTransactionIds().isEmpty()) {
            var txns = transactionRepository.findAllById(dto.getTransactionIds());
            investigationCase.setLinkedTransactions(txns);
        }

        Case saved = caseRepository.save(investigationCase);

        auditService.recordEvent("CASES", "CREATE_CASE", "CASE", saved.getId().toString(),
                "New investigation case " + saved.getCaseNumber() + " created for customer " + customer.getFullName());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<CaseDto> searchCases(String status, String priority, UUID customerId, Pageable pageable) {
        return caseRepository.searchCases(status, priority, customerId, pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public CaseDto getCaseById(UUID id) {
        return caseRepository.findById(id)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", id.toString()));
    }

    @Transactional
    public CaseDto addNote(UUID caseId, CaseNoteDto dto) {
        Case investigationCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId.toString()));

        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessRuleException("No authenticated user"));

        var author = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId.toString()));

        CaseNote note = new CaseNote();
        note.setInvestigationCase(investigationCase);
        note.setAuthor(author);
        note.setNoteText(dto.getContent());
        note.setInternalOnly(!dto.isConfidential());
        caseNoteRepository.save(note);

        auditService.recordEvent("CASES", "ADD_NOTE", "CASE", caseId.toString(),
                "Note added by " + author.getUsername() + " to case " + investigationCase.getCaseNumber());

        return mapToDto(investigationCase);
    }

    @Transactional
    public CaseDto escalateToMlro(UUID caseId, String reason) {
        Case investigationCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId.toString()));

        if (List.of("CLOSED_CLEARED", "CLOSED_SUSPICIOUS", "FILED_STR").contains(investigationCase.getStatus())) {
            throw new BusinessRuleException("Cannot escalate closed case " + investigationCase.getCaseNumber());
        }

        investigationCase.setStatus("ESCALATED_MLRO");
        investigationCase.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(investigationCase);

        auditService.recordEvent("CASES", "ESCALATE_TO_MLRO", "CASE", caseId.toString(),
                "Case " + investigationCase.getCaseNumber() + " escalated to MLRO. Reason: " + reason);

        return mapToDto(saved);
    }

    @Transactional
    public CaseDto closeCase(UUID caseId, CaseClosureDto dto) {
        Case investigationCase = caseRepository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case", "id", caseId.toString()));

        if (List.of("CLOSED_CLEARED", "CLOSED_SUSPICIOUS", "FILED_STR").contains(investigationCase.getStatus())) {
            throw new BusinessRuleException("Case " + investigationCase.getCaseNumber() + " is already closed.");
        }

        String newStatus = switch (dto.getDecision()) {
            case "CLEARED"    -> "CLOSED_CLEARED";
            case "SUSPICIOUS" -> "CLOSED_SUSPICIOUS";
            case "FILED_STR"  -> "FILED_STR";
            default -> throw new BusinessRuleException("Invalid closure decision: " + dto.getDecision());
        };

        investigationCase.setStatus(newStatus);
        investigationCase.setClosureDecision(dto.getDecision());
        investigationCase.setRationale(dto.getRationale());
        if (dto.getInvestigationSummary() != null) {
            investigationCase.setInvestigationSummary(dto.getInvestigationSummary());
        }
        investigationCase.setClosedAt(Instant.now());
        investigationCase.setUpdatedAt(Instant.now());
        Case saved = caseRepository.save(investigationCase);

        auditService.recordEvent("CASES", "CLOSE_CASE", "CASE", caseId.toString(),
                "Case " + investigationCase.getCaseNumber() + " closed with decision: " + dto.getDecision());

        return mapToDto(saved);
    }

    public CaseDto mapToDto(Case c) {
        CaseDto dto = new CaseDto();
        dto.setId(c.getId());
        dto.setCaseNumber(c.getCaseNumber());
        dto.setTitle(c.getTitle());
        dto.setPriority(c.getPriority());
        dto.setStatus(c.getStatus());
        dto.setCustomerId(c.getCustomer().getId());
        dto.setCustomerNumber(c.getCustomer().getCustomerNumber());
        dto.setCustomerName(c.getCustomer().getFullName());
        if (c.getPrimaryAnalyst() != null) {
            dto.setPrimaryAnalystName(c.getPrimaryAnalyst().getFullName());
        }
        dto.setSlaDueAt(c.getSlaDueAt());
        dto.setSlaBreached(c.getSlaDueAt() != null && Instant.now().isAfter(c.getSlaDueAt()) && c.getClosedAt() == null);
        dto.setAlertsCount(c.getLinkedAlerts() != null ? c.getLinkedAlerts().size() : 0);
        dto.setTransactionsCount(c.getLinkedTransactions() != null ? c.getLinkedTransactions().size() : 0);
        dto.setEvidenceCount(c.getEvidenceList() != null ? c.getEvidenceList().size() : 0);
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
