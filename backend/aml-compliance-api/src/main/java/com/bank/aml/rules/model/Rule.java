package com.bank.aml.rules.model;

import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "rule_code", nullable = false, unique = true, length = 50)
    private String ruleCode; // TM-STR-01, TM-VEL-02, TM-GEO-03, TM-DOR-04, TM-LGT-05

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType; // TRANSACTION_MONITORING, CUSTOMER_RISK, SCREENING

    @Column(nullable = false, length = 50)
    private String category; // STRUCTURING, VELOCITY, RAPID_MOVEMENT, HIGH_RISK_JURISDICTION, DORMANT_ACTIVITY, THRESHOLD

    @Column(nullable = false)
    private int version = 1;

    @Column(name = "parameters_json", columnDefinition = "jsonb", nullable = false)
    private String parametersJson;

    @Column(name = "risk_weight", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskWeight = BigDecimal.valueOf(50.0);

    @Column(nullable = false, length = 20)
    private String severity = "HIGH"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_checker")
    private User approvedByChecker;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom = LocalDate.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Rule() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }
    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
    public BigDecimal getRiskWeight() { return riskWeight; }
    public void setRiskWeight(BigDecimal riskWeight) { this.riskWeight = riskWeight; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public User getApprovedByChecker() { return approvedByChecker; }
    public void setApprovedByChecker(User approvedByChecker) { this.approvedByChecker = approvedByChecker; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
