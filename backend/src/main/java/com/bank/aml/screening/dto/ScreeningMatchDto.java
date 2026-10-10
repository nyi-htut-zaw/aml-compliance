package com.bank.aml.screening.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class ScreeningMatchDto {
    private UUID id;
    private UUID customerId;
    private String customerName;
    private UUID watchlistEntryId;
    private String primaryName;
    private String aliases;
    private String sourceName;
    private String sourceCode;
    private String matchType;
    private BigDecimal matchScore;
    private String matchingAlgorithm;
    private String matchedTerm;
    private String pepCategory;
    private String designatorPrograms;
    private String disposition;
    private String dispositionNotes;
    private String dispositionByUsername;
    private Instant dispositionAt;
    private Instant createdAt;

    public ScreeningMatchDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public UUID getWatchlistEntryId() { return watchlistEntryId; }
    public void setWatchlistEntryId(UUID watchlistEntryId) { this.watchlistEntryId = watchlistEntryId; }
    public String getPrimaryName() { return primaryName; }
    public void setPrimaryName(String primaryName) { this.primaryName = primaryName; }
    public String getAliases() { return aliases; }
    public void setAliases(String aliases) { this.aliases = aliases; }
    public String getSourceName() { return sourceName; }
    public void setSourceName(String sourceName) { this.sourceName = sourceName; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public BigDecimal getMatchScore() { return matchScore; }
    public void setMatchScore(BigDecimal matchScore) { this.matchScore = matchScore; }
    public String getMatchingAlgorithm() { return matchingAlgorithm; }
    public void setMatchingAlgorithm(String matchingAlgorithm) { this.matchingAlgorithm = matchingAlgorithm; }
    public String getMatchedTerm() { return matchedTerm; }
    public void setMatchedTerm(String matchedTerm) { this.matchedTerm = matchedTerm; }
    public String getPepCategory() { return pepCategory; }
    public void setPepCategory(String pepCategory) { this.pepCategory = pepCategory; }
    public String getDesignatorPrograms() { return designatorPrograms; }
    public void setDesignatorPrograms(String designatorPrograms) { this.designatorPrograms = designatorPrograms; }
    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public String getDispositionNotes() { return dispositionNotes; }
    public void setDispositionNotes(String dispositionNotes) { this.dispositionNotes = dispositionNotes; }
    public String getDispositionByUsername() { return dispositionByUsername; }
    public void setDispositionByUsername(String dispositionByUsername) { this.dispositionByUsername = dispositionByUsername; }
    public Instant getDispositionAt() { return dispositionAt; }
    public void setDispositionAt(Instant dispositionAt) { this.dispositionAt = dispositionAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
