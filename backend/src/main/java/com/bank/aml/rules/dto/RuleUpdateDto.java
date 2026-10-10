package com.bank.aml.rules.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class RuleUpdateDto {
    @NotNull
    private String parametersJson;

    private BigDecimal riskWeight;

    private String severity;

    private Boolean active;

    public RuleUpdateDto() {}

    public String getParametersJson() { return parametersJson; }
    public void setParametersJson(String parametersJson) { this.parametersJson = parametersJson; }
    public BigDecimal getRiskWeight() { return riskWeight; }
    public void setRiskWeight(BigDecimal riskWeight) { this.riskWeight = riskWeight; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
