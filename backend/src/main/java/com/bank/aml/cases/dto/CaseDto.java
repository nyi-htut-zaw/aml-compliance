package com.bank.aml.cases.dto;

import java.time.Instant;
import java.util.UUID;

public class CaseDto {
    private UUID id;
    private String caseNumber;
    private String title;
    private String priority;
    private String status;
    private UUID customerId;
    private String customerNumber;
    private String customerName;
    private String primaryAnalystName;
    private Instant slaDueAt;
    private boolean slaBreached;
    private int alertsCount;
    private int transactionsCount;
    private int evidenceCount;
    private Instant createdAt;

    public CaseDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getPrimaryAnalystName() { return primaryAnalystName; }
    public void setPrimaryAnalystName(String primaryAnalystName) { this.primaryAnalystName = primaryAnalystName; }
    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public boolean isSlaBreached() { return slaBreached; }
    public void setSlaBreached(boolean slaBreached) { this.slaBreached = slaBreached; }
    public int getAlertsCount() { return alertsCount; }
    public void setAlertsCount(int alertsCount) { this.alertsCount = alertsCount; }
    public int getTransactionsCount() { return transactionsCount; }
    public void setTransactionsCount(int transactionsCount) { this.transactionsCount = transactionsCount; }
    public int getEvidenceCount() { return evidenceCount; }
    public void setEvidenceCount(int evidenceCount) { this.evidenceCount = evidenceCount; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
