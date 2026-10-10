package com.bank.aml.screening.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.UUID;

public class ScreeningRequestDto {

    @NotBlank(message = "Search name is required")
    private String name;

    private String entityType; // INDIVIDUAL, ENTITY
    private String dateOfBirth;
    private String countryCode;
    private Double thresholdScore = 80.0;
    private UUID customerId;

    public ScreeningRequestDto() {}

    public ScreeningRequestDto(String name, String entityType, String dateOfBirth, String countryCode, Double thresholdScore, UUID customerId) {
        this.name = name;
        this.entityType = entityType;
        this.dateOfBirth = dateOfBirth;
        this.countryCode = countryCode;
        this.thresholdScore = thresholdScore != null ? thresholdScore : 80.0;
        this.customerId = customerId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public Double getThresholdScore() { return thresholdScore; }
    public void setThresholdScore(Double thresholdScore) { this.thresholdScore = thresholdScore; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
}
