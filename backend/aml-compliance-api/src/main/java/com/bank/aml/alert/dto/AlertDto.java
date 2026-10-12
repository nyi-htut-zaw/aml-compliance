package com.bank.aml.alert.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class AlertDto {
    private UUID id;
    private String alertNumber;
    private String title;
    private UUID customerId;
    private String customerName;
    private UUID accountId;
    private UUID transactionId;
    private String ruleCode;
    private String ruleName;
    private String triggerEvent;
    private String severity;
    private BigDecimal score;
    private String status;
    private UUID assignedToUserId;
    private String assignedToUsername;
    private Instant slaDueAt;
    private String triggerPayloadJson;
    private String dispositionNotes;
    private Instant closedAt;
    private Instant createdAt;
    private Instant updatedAt;

    public AlertDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAlertNumber() { return alertNumber; }
    public void setAlertNumber(String alertNumber) { this.alertNumber = alertNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public UUID getTransactionId() { return transactionId; }
    public void setTransactionId(UUID transactionId) { this.transactionId = transactionId; }
    public String getRuleCode() { return ruleCode; }
    public void setRuleCode(String ruleCode) { this.ruleCode = ruleCode; }
    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }
    public String getTriggerEvent() { return triggerEvent; }
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getAssignedToUserId() { return assignedToUserId; }
    public void setAssignedToUserId(UUID assignedToUserId) { this.assignedToUserId = assignedToUserId; }
    public String getAssignedToUsername() { return assignedToUsername; }
    public void setAssignedToUsername(String assignedToUsername) { this.assignedToUsername = assignedToUsername; }
    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public String getTriggerPayloadJson() { return triggerPayloadJson; }
    public void setTriggerPayloadJson(String triggerPayloadJson) { this.triggerPayloadJson = triggerPayloadJson; }
    public String getDispositionNotes() { return dispositionNotes; }
    public void setDispositionNotes(String dispositionNotes) { this.dispositionNotes = dispositionNotes; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
