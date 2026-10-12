package com.bank.aml.audit.controller;

import com.bank.aml.audit.model.AuditEvent;
import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/audit", "/audit"})
@Tag(name = "Audit & Compliance", description = "Append-only business audit trail and compliance inspection")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @GetMapping({"", "/events"})
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'AUDITOR')")
    @Operation(summary = "Query append-only audit trail with module filtering")
    public ApiResponse<PagedResponse<AuditEvent>> getAuditEvents(
            @RequestParam(required = false) String module,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<AuditEvent> eventPage = auditService.getAuditEvents(
                module,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"))
        );
        return ApiResponse.ok(PagedResponse.of(eventPage));
    }
}
