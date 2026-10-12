package com.bank.aml.customer.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.customer.dto.KycDecisionDto;
import com.bank.aml.customer.dto.KycReviewDto;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.customer.model.KycReview;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.customer.repository.KycReviewRepository;
import com.bank.aml.iam.model.User;
import com.bank.aml.iam.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KycService {

    private final KycReviewRepository kycReviewRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public KycService(
            KycReviewRepository kycReviewRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.kycReviewRepository = kycReviewRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<KycReviewDto> getReviewsForCustomer(UUID customerId) {
        return kycReviewRepository.findByCustomerIdOrderByReviewDateDesc(customerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<KycReviewDto> getPendingReviews(Pageable pageable) {
        return kycReviewRepository.findByStatus("IN_PROGRESS", pageable).map(this::mapToDto);
    }

    @Transactional
    public KycReviewDto decideReview(UUID reviewId, KycDecisionDto decisionDto) {
        KycReview review = kycReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("KycReview", "id", reviewId));

        UUID approverId = SecurityUtils.getCurrentUserId().orElse(null);
        User approver = approverId != null ? userRepository.findById(approverId).orElse(null) : null;

        review.setApprover(approver);
        review.setStatus(decisionDto.getDecision());
        review.setDecisionNotes(decisionDto.getDecisionNotes());

        Customer customer = review.getCustomer();

        if ("APPROVED".equalsIgnoreCase(decisionDto.getDecision())) {
            customer.setKycStatus("APPROVED");
            if (decisionDto.getNewRiskRating() != null) {
                customer.setRiskRating(decisionDto.getNewRiskRating());
            }
            int months = switch (customer.getRiskRating()) {
                case "CRITICAL" -> 3;
                case "HIGH" -> 6;
                default -> 12;
            };
            customer.setLastKycReviewDate(LocalDate.now());
            customer.setNextKycReviewDate(LocalDate.now().plusMonths(months));
            review.setNextReviewDate(customer.getNextKycReviewDate());
        } else if ("REJECTED".equalsIgnoreCase(decisionDto.getDecision())) {
            customer.setKycStatus("REJECTED");
            customer.setAccountStatus("BLOCKED");
        } else if ("ESCALATED_EDD".equalsIgnoreCase(decisionDto.getDecision())) {
            customer.setKycStatus("UNDER_EDD");
            customer.setRiskRating("HIGH");
        }

        customerRepository.save(customer);
        KycReview saved = kycReviewRepository.save(review);

        auditService.recordEvent("KYC", "REVIEW_DECISION", "KYC_REVIEW", saved.getId().toString(),
                "Decision " + decisionDto.getDecision() + " applied to customer " + customer.getCustomerNumber());

        return mapToDto(saved);
    }

    public KycReviewDto mapToDto(KycReview review) {
        KycReviewDto dto = new KycReviewDto();
        dto.setId(review.getId());
        dto.setCustomerId(review.getCustomer().getId());
        dto.setCustomerNumber(review.getCustomer().getCustomerNumber());
        dto.setCustomerName(review.getCustomer().getFullName());
        dto.setReviewType(review.getReviewType());
        dto.setDueDiligenceLevel(review.getDueDiligenceLevel());
        dto.setStatus(review.getStatus());
        dto.setRiskScore(review.getRiskScore());
        dto.setFindings(review.getFindings());
        dto.setDecisionNotes(review.getDecisionNotes());
        dto.setReviewDate(review.getReviewDate());
        dto.setNextReviewDate(review.getNextReviewDate());
        if (review.getReviewer() != null) {
            dto.setReviewerName(review.getReviewer().getFullName());
        }
        if (review.getApprover() != null) {
            dto.setApproverName(review.getApprover().getFullName());
        }
        return dto;
    }
}
