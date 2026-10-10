package com.bank.aml.rules.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class RuleDto {
    private UUID id;
    private String ruleCode;
    private String name;
    private String description;
    private String ruleType;
    private String category;
    private int version;
    private String parametersJson;
    private BigDecimal riskWeight;
    private String severity;
    private boolean active;
    private LocalDate effectiveFrom;

    public RuleDto() {}

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
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
}
