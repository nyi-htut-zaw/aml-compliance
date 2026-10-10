package com.bank.aml.transaction.dto;

import java.util.ArrayList;
import java.util.List;

public class BatchImportResultDto {
    private int totalRows;
    private int processedCount;
    private int duplicatesSkipped;
    private int alertsGenerated;
    private List<String> errors = new ArrayList<>();

    public BatchImportResultDto() {}

    public int getTotalRows() { return totalRows; }
    public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
    public int getProcessedCount() { return processedCount; }
    public void setProcessedCount(int processedCount) { this.processedCount = processedCount; }
    public int getDuplicatesSkipped() { return duplicatesSkipped; }
    public void setDuplicatesSkipped(int duplicatesSkipped) { this.duplicatesSkipped = duplicatesSkipped; }
    public int getAlertsGenerated() { return alertsGenerated; }
    public void setAlertsGenerated(int alertsGenerated) { this.alertsGenerated = alertsGenerated; }
    public List<String> getErrors() { return errors; }
    public void setErrors(List<String> errors) { this.errors = errors; }
}
