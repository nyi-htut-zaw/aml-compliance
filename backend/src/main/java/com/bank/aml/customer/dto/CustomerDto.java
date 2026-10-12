package com.bank.aml.customer.dto;

import java.time.LocalDate;
import java.util.UUID;

public class CustomerDto {
    private UUID id;
    private String customerNumber;
    private String customerType;
    private String fullName;
    private String fullNameMyanmar;
    private String tradeName;
    private String jurisdictionCountry;
    private LocalDate dateOfBirthOrIncorp;
    private String taxIdentifier;
    private String riskRating;
    private String kycStatus;
    private String accountStatus;
    private LocalDate onboardingDate;
    private LocalDate nextKycReviewDate;
    private boolean pep;
    private boolean sanctionsHit;
    private boolean adverseMediaHit;
    private String branchCode;

    public CustomerDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getFullNameMyanmar() { return fullNameMyanmar; }
    public void setFullNameMyanmar(String fullNameMyanmar) { this.fullNameMyanmar = fullNameMyanmar; }
    public String getTradeName() { return tradeName; }
    public void setTradeName(String tradeName) { this.tradeName = tradeName; }
    public String getJurisdictionCountry() { return jurisdictionCountry; }
    public void setJurisdictionCountry(String jurisdictionCountry) { this.jurisdictionCountry = jurisdictionCountry; }
    public LocalDate getDateOfBirthOrIncorp() { return dateOfBirthOrIncorp; }
    public void setDateOfBirthOrIncorp(LocalDate dateOfBirthOrIncorp) { this.dateOfBirthOrIncorp = dateOfBirthOrIncorp; }
    public String getTaxIdentifier() { return taxIdentifier; }
    public void setTaxIdentifier(String taxIdentifier) { this.taxIdentifier = taxIdentifier; }
    public String getRiskRating() { return riskRating; }
    public void setRiskRating(String riskRating) { this.riskRating = riskRating; }
    public String getKycStatus() { return kycStatus; }
    public void setKycStatus(String kycStatus) { this.kycStatus = kycStatus; }
    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }
    public LocalDate getOnboardingDate() { return onboardingDate; }
    public void setOnboardingDate(LocalDate onboardingDate) { this.onboardingDate = onboardingDate; }
    public LocalDate getNextKycReviewDate() { return nextKycReviewDate; }
    public void setNextKycReviewDate(LocalDate nextKycReviewDate) { this.nextKycReviewDate = nextKycReviewDate; }
    public boolean isPep() { return pep; }
    public void setPep(boolean pep) { this.pep = pep; }
    public boolean isSanctionsHit() { return sanctionsHit; }
    public void setSanctionsHit(boolean sanctionsHit) { this.sanctionsHit = sanctionsHit; }
    public boolean isAdverseMediaHit() { return adverseMediaHit; }
    public void setAdverseMediaHit(boolean adverseMediaHit) { this.adverseMediaHit = adverseMediaHit; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
}
