package com.bank.aml.cases.controller;

import com.bank.aml.cases.dto.CaseClosureDto;
import com.bank.aml.cases.dto.CaseCreateDto;
import com.bank.aml.cases.dto.CaseDto;
import com.bank.aml.cases.dto.CaseNoteDto;
import com.bank.aml.cases.service.CaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/cases")
@Tag(name = "Case Management", description = "Investigation case lifecycle management APIs")
@SecurityRequirement(name = "bearerAuth")
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Create a new investigation case from alert(s) or manually")
    public ResponseEntity<CaseDto> createCase(@Valid @RequestBody CaseCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(caseService.createCase(dto));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Search and list cases with filtering and pagination")
    public ResponseEntity<Page<CaseDto>> searchCases(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) UUID customerId,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(caseService.searchCases(status, priority, customerId, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Get case workspace detail by ID")
    public ResponseEntity<CaseDto> getCaseById(@PathVariable UUID id) {
        return ResponseEntity.ok(caseService.getCaseById(id));
    }

    @PostMapping("/{id}/notes")
    @PreAuthorize("hasAnyRole('ANALYST', 'SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Add an investigator note to a case")
    public ResponseEntity<CaseDto> addNote(@PathVariable UUID id, @Valid @RequestBody CaseNoteDto dto) {
        return ResponseEntity.ok(caseService.addNote(id, dto));
    }

    @PostMapping("/{id}/escalate")
    @PreAuthorize("hasAnyRole('SENIOR_ANALYST', 'COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Escalate case to MLRO for review")
    public ResponseEntity<CaseDto> escalate(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(caseService.escalateToMlro(id, reason));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Close a case with a compliance decision (Maker-Checker sign-off required)")
    public ResponseEntity<CaseDto> closeCase(@PathVariable UUID id, @Valid @RequestBody CaseClosureDto dto) {
        return ResponseEntity.ok(caseService.closeCase(id, dto));
    }
}
