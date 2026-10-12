package com.bank.aml.rules.controller;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.rules.dto.RuleDto;
import com.bank.aml.rules.dto.RuleUpdateDto;
import com.bank.aml.rules.model.Rule;
import com.bank.aml.rules.repository.RuleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rules")
@Tag(name = "Rules Administration", description = "TM and screening rule parameter management APIs")
@SecurityRequirement(name = "bearerAuth")
public class RuleController {

    private final RuleRepository ruleRepository;
    private final AuditService auditService;

    public RuleController(RuleRepository ruleRepository, AuditService auditService) {
        this.ruleRepository = ruleRepository;
        this.auditService = auditService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN', 'ANALYST', 'SENIOR_ANALYST')")
    @Operation(summary = "List all configured rules")
    public ResponseEntity<List<RuleDto>> listRules(@RequestParam(required = false) String ruleType) {
        List<Rule> rules = ruleType != null
                ? ruleRepository.findByRuleTypeAndActiveTrue(ruleType)
                : ruleRepository.findAll();
        return ResponseEntity.ok(rules.stream().map(this::toDto).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN', 'ANALYST', 'SENIOR_ANALYST')")
    @Operation(summary = "Get a single rule by ID")
    public ResponseEntity<RuleDto> getRule(@PathVariable UUID id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule", "id", id.toString()));
        return ResponseEntity.ok(toDto(rule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Update rule parameters, risk weight, severity, or activation status. Requires COMPLIANCE_OFFICER or higher.")
    public ResponseEntity<RuleDto> updateRule(@PathVariable UUID id, @Valid @RequestBody RuleUpdateDto dto) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule", "id", id.toString()));

        if (dto.getParametersJson() != null) rule.setParametersJson(dto.getParametersJson());
        if (dto.getRiskWeight() != null) rule.setRiskWeight(dto.getRiskWeight());
        if (dto.getSeverity() != null) rule.setSeverity(dto.getSeverity());
        if (dto.getActive() != null) rule.setActive(dto.getActive());
        rule.setVersion(rule.getVersion() + 1);
        rule.setUpdatedAt(Instant.now());

        Rule saved = ruleRepository.save(rule);

        auditService.recordEvent("RULES", "UPDATE_RULE", "RULE", id.toString(),
                "Rule " + rule.getRuleCode() + " parameters updated. New version: " + saved.getVersion());

        return ResponseEntity.ok(toDto(saved));
    }

    @PostMapping("/{id}/toggle")
    @PreAuthorize("hasAnyRole('COMPLIANCE_OFFICER', 'MLRO', 'ADMIN')")
    @Operation(summary = "Toggle a rule's active/inactive status")
    public ResponseEntity<RuleDto> toggleRule(@PathVariable UUID id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rule", "id", id.toString()));
        rule.setActive(!rule.isActive());
        rule.setUpdatedAt(Instant.now());
        Rule saved = ruleRepository.save(rule);
        auditService.recordEvent("RULES", rule.isActive() ? "RULE_ENABLED" : "RULE_DISABLED", "RULE", id.toString(),
                "Rule " + rule.getRuleCode() + " is now " + (rule.isActive() ? "ACTIVE" : "INACTIVE"));
        return ResponseEntity.ok(toDto(saved));
    }

    private RuleDto toDto(Rule r) {
        RuleDto dto = new RuleDto();
        dto.setId(r.getId());
        dto.setRuleCode(r.getRuleCode());
        dto.setName(r.getName());
        dto.setDescription(r.getDescription());
        dto.setRuleType(r.getRuleType());
        dto.setCategory(r.getCategory());
        dto.setVersion(r.getVersion());
        dto.setParametersJson(r.getParametersJson());
        dto.setRiskWeight(r.getRiskWeight());
        dto.setSeverity(r.getSeverity());
        dto.setActive(r.isActive());
        dto.setEffectiveFrom(r.getEffectiveFrom());
        return dto;
    }
}
