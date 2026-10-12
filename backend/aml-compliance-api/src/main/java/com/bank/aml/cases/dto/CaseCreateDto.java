package com.bank.aml.cases.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class CaseCreateDto {
    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @NotNull
    private UUID customerId;

    private List<UUID> alertIds;
    private List<UUID> transactionIds;

    public CaseCreateDto() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public List<UUID> getAlertIds() { return alertIds; }
    public void setAlertIds(List<UUID> alertIds) { this.alertIds = alertIds; }
    public List<UUID> getTransactionIds() { return transactionIds; }
    public void setTransactionIds(List<UUID> transactionIds) { this.transactionIds = transactionIds; }
}
