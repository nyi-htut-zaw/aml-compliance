package com.bank.aml.customer.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class BeneficialOwnerDto {
    private UUID id;
    private UUID entityCustomerId;
    private UUID ownerCustomerId;
    private String ownerName;
    private String nationality;
    private BigDecimal ownershipPercentage;
    private String controlType;
    private boolean ubo;
    private String verificationStatus;
    private LocalDate effectiveFrom;

    public BeneficialOwnerDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getEntityCustomerId() { return entityCustomerId; }
    public void setEntityCustomerId(UUID entityCustomerId) { this.entityCustomerId = entityCustomerId; }
    public UUID getOwnerCustomerId() { return ownerCustomerId; }
    public void setOwnerCustomerId(UUID ownerCustomerId) { this.ownerCustomerId = ownerCustomerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public BigDecimal getOwnershipPercentage() { return ownershipPercentage; }
    public void setOwnershipPercentage(BigDecimal ownershipPercentage) { this.ownershipPercentage = ownershipPercentage; }
    public String getControlType() { return controlType; }
    public void setControlType(String controlType) { this.controlType = controlType; }
    public boolean isUbo() { return ubo; }
    public void setUbo(boolean ubo) { this.ubo = ubo; }
    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
}
