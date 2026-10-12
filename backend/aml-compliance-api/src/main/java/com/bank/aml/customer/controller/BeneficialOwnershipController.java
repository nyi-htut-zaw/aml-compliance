package com.bank.aml.customer.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.customer.dto.BeneficialOwnerDto;
import com.bank.aml.customer.dto.UboGraphDto;
import com.bank.aml.customer.service.BeneficialOwnershipService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/customers/{customerId}/beneficial-owners", "/customers/{customerId}/beneficial-owners"})
@Tag(name = "Beneficial Ownership", description = "Corporate shareholding, control structures, and UBO relationship graphs")
public class BeneficialOwnershipController {

    private final BeneficialOwnershipService beneficialOwnershipService;

    public BeneficialOwnershipController(BeneficialOwnershipService beneficialOwnershipService) {
        this.beneficialOwnershipService = beneficialOwnershipService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "List registered beneficial owners and control parties for a legal entity")
    public ApiResponse<List<BeneficialOwnerDto>> getBeneficialOwners(@PathVariable UUID customerId) {
        List<BeneficialOwnerDto> owners = beneficialOwnershipService.getBeneficialOwnersForEntity(customerId);
        return ApiResponse.ok(owners);
    }

    @GetMapping("/graph")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Generate nodes and edges for interactive UBO ownership network graph")
    public ApiResponse<UboGraphDto> getUboGraph(@PathVariable UUID customerId) {
        UboGraphDto graph = beneficialOwnershipService.calculateUboGraph(customerId);
        return ApiResponse.ok(graph);
    }
}
