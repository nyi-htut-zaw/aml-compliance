package com.bank.aml.iam.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.iam.dto.ApprovalResponseDto;
import com.bank.aml.iam.dto.MakerCheckerDecisionDto;
import com.bank.aml.iam.model.ApprovalRequest;
import com.bank.aml.iam.model.User;
import com.bank.aml.iam.repository.ApprovalRequestRepository;
import com.bank.aml.iam.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class MakerCheckerService {

    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public MakerCheckerService(
            ApprovalRequestRepository approvalRequestRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.approvalRequestRepository = approvalRequestRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public ApprovalResponseDto requestApproval(String entityType, String entityId, String actionType,
                                               String makerComments, String payloadSnapshot) {
        UUID makerId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessRuleException("Authenticated maker user required"));

        User maker = userRepository.findById(makerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", makerId));

        ApprovalRequest request = new ApprovalRequest();
        request.setEntityType(entityType);
        request.setEntityId(entityId);
        request.setActionType(actionType);
        request.setMakerUser(maker);
        request.setMakerComments(makerComments);
        request.setPayloadSnapshot(payloadSnapshot);
        request.setStatus("PENDING");

        ApprovalRequest saved = approvalRequestRepository.save(request);

        auditService.recordEvent("MAKER_CHECKER", "SUBMIT_FOR_APPROVAL", entityType, entityId,
                "Maker " + maker.getUsername() + " submitted " + actionType + " request for approval");

        return mapToDto(saved);
    }

    @Transactional
    public ApprovalResponseDto decideApproval(UUID approvalId, MakerCheckerDecisionDto decision) {
        ApprovalRequest request = approvalRequestRepository.findById(approvalId)
                .orElseThrow(() -> new ResourceNotFoundException("ApprovalRequest", "id", approvalId));

        if (!"PENDING".equalsIgnoreCase(request.getStatus())) {
            throw new BusinessRuleException("Approval request is already resolved with status: " + request.getStatus());
        }

        UUID checkerId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new BusinessRuleException("Authenticated checker user required"));

        // Segregation of Duties Check
        if (request.getMakerUser().getId().equals(checkerId)) {
            throw new BusinessRuleException("Segregation of duties violation: Maker cannot approve or reject their own submission.");
        }

        User checker = userRepository.findById(checkerId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", checkerId));

        request.setCheckerUser(checker);
        request.setStatus(decision.getDecision().toUpperCase());
        request.setCheckerComments(decision.getComments());
        request.setResolvedAt(Instant.now());

        ApprovalRequest saved = approvalRequestRepository.save(request);

        auditService.recordEvent("MAKER_CHECKER", decision.getDecision().toUpperCase(),
                request.getEntityType(), request.getEntityId(),
                "Checker " + checker.getUsername() + " decided " + decision.getDecision() +
                        ". Comments: " + decision.getComments());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ApprovalResponseDto> getPendingApprovals(Pageable pageable) {
        return approvalRequestRepository.findByStatus("PENDING", pageable).map(this::mapToDto);
    }

    public ApprovalResponseDto mapToDto(ApprovalRequest entity) {
        ApprovalResponseDto dto = new ApprovalResponseDto();
        dto.setId(entity.getId());
        dto.setEntityType(entity.getEntityType());
        dto.setEntityId(entity.getEntityId());
        dto.setActionType(entity.getActionType());
        dto.setStatus(entity.getStatus());
        dto.setMakerComments(entity.getMakerComments());
        dto.setCheckerComments(entity.getCheckerComments());
        dto.setPayloadSnapshot(entity.getPayloadSnapshot());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setResolvedAt(entity.getResolvedAt());

        if (entity.getMakerUser() != null) {
            dto.setMakerUserId(entity.getMakerUser().getId());
            dto.setMakerUsername(entity.getMakerUser().getUsername());
            dto.setMakerFullName(entity.getMakerUser().getFullName());
        }

        if (entity.getCheckerUser() != null) {
            dto.setCheckerUserId(entity.getCheckerUser().getId());
            dto.setCheckerUsername(entity.getCheckerUser().getUsername());
            dto.setCheckerFullName(entity.getCheckerUser().getFullName());
        }

        return dto;
    }
}
