package com.bank.aml.iam.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import com.bank.aml.iam.dto.ApprovalResponseDto;
import com.bank.aml.iam.dto.MakerCheckerDecisionDto;
import com.bank.aml.iam.service.MakerCheckerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/approvals", "/approvals"})
@Tag(name = "Maker-Checker Approvals", description = "Dual-control segregation of duties workflow for compliance actions")
public class MakerCheckerController {

    private final MakerCheckerService makerCheckerService;

    public MakerCheckerController(MakerCheckerService makerCheckerService) {
        this.makerCheckerService = makerCheckerService;
    }

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST')")
    @Operation(summary = "Get pending dual-authorization approval requests")
    public ApiResponse<PagedResponse<ApprovalResponseDto>> getPendingApprovals(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ApprovalResponseDto> pending = makerCheckerService.getPendingApprovals(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return ApiResponse.ok(PagedResponse.of(pending));
    }

    @PostMapping("/{id}/decide")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO')")
    @Operation(summary = "Checker decides approval request (Segregation of Duties: Maker cannot approve)")
    public ApiResponse<ApprovalResponseDto> decide(
            @PathVariable UUID id,
            @Valid @RequestBody MakerCheckerDecisionDto decision) {
        ApprovalResponseDto result = makerCheckerService.decideApproval(id, decision);
        return ApiResponse.ok("Decision recorded successfully", result);
    }
}
