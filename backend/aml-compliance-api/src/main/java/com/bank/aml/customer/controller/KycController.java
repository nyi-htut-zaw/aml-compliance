package com.bank.aml.customer.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import com.bank.aml.customer.dto.KycDecisionDto;
import com.bank.aml.customer.dto.KycReviewDto;
import com.bank.aml.customer.service.KycService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/kyc", "/kyc"})
@Tag(name = "CDD / EDD Due Diligence", description = "Customer due diligence review cycles and compliance approvals")
public class KycController {

    private final KycService kycService;

    public KycController(KycService kycService) {
        this.kycService = kycService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST')")
    @Operation(summary = "Get pending CDD and EDD reviews queue")
    public ApiResponse<PagedResponse<KycReviewDto>> getPendingReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<KycReviewDto> reviews = kycService.getPendingReviews(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "reviewDate"))
        );
        return ApiResponse.ok(PagedResponse.of(reviews));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Get historical KYC reviews for a specific customer")
    public ApiResponse<List<KycReviewDto>> getCustomerReviews(@PathVariable UUID customerId) {
        List<KycReviewDto> reviews = kycService.getReviewsForCustomer(customerId);
        return ApiResponse.ok(reviews);
    }

    @PostMapping("/reviews/{reviewId}/decide")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST')")
    @Operation(summary = "Submit sign-off decision (Approve/Reject/Escalate) on a KYC review")
    public ApiResponse<KycReviewDto> decideReview(
            @PathVariable UUID reviewId,
            @Valid @RequestBody KycDecisionDto decisionDto) {
        KycReviewDto result = kycService.decideReview(reviewId, decisionDto);
        return ApiResponse.ok("KYC Review decision processed successfully", result);
    }
}
