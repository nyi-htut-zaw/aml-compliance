package com.bank.aml.customer.dto;

import com.bank.aml.customer.model.BeneficialOwner;
import com.bank.aml.customer.model.CustomerAddress;
import com.bank.aml.customer.model.CustomerIdentifier;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CustomerDetailDto {
    private UUID id;
    private String customerNumber;
    private String customerType;
    private String fullName;
    private String tradeName;
    private String jurisdictionCountry;
    private LocalDate dateOfBirthOrIncorp;
    private String taxIdentifier;
    private String riskRating;
    private String kycStatus;
    private String accountStatus;
    private LocalDate onboardingDate;
    private LocalDate lastKycReviewDate;
    private LocalDate nextKycReviewDate;
    private String sourceOfWealth;
    private String sourceOfFunds;
    private String occupationOrIndustry;
    private boolean pep;
    private boolean sanctionsHit;
    private boolean adverseMediaHit;
    private String branchName;
    private String branchCode;
    private List<CustomerIdentifier> identifiers;
    private List<CustomerAddress> addresses;
    private List<BeneficialOwnerDto> beneficialOwners;

    public CustomerDetailDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
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
    public LocalDate getLastKycReviewDate() { return lastKycReviewDate; }
    public void setLastKycReviewDate(LocalDate lastKycReviewDate) { this.lastKycReviewDate = lastKycReviewDate; }
    public LocalDate getNextKycReviewDate() { return nextKycReviewDate; }
    public void setNextKycReviewDate(LocalDate nextKycReviewDate) { this.nextKycReviewDate = nextKycReviewDate; }
    public String getSourceOfWealth() { return sourceOfWealth; }
    public void setSourceOfWealth(String sourceOfWealth) { this.sourceOfWealth = sourceOfWealth; }
    public String getSourceOfFunds() { return sourceOfFunds; }
    public void setSourceOfFunds(String sourceOfFunds) { this.sourceOfFunds = sourceOfFunds; }
    public String getOccupationOrIndustry() { return occupationOrIndustry; }
    public void setOccupationOrIndustry(String occupationOrIndustry) { this.occupationOrIndustry = occupationOrIndustry; }
    public boolean isPep() { return pep; }
    public void setPep(boolean pep) { this.pep = pep; }
    public boolean isSanctionsHit() { return sanctionsHit; }
    public void setSanctionsHit(boolean sanctionsHit) { this.sanctionsHit = sanctionsHit; }
    public boolean isAdverseMediaHit() { return adverseMediaHit; }
    public void setAdverseMediaHit(boolean adverseMediaHit) { this.adverseMediaHit = adverseMediaHit; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public List<CustomerIdentifier> getIdentifiers() { return identifiers; }
    public void setIdentifiers(List<CustomerIdentifier> identifiers) { this.identifiers = identifiers; }
    public List<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses; }
    public List<BeneficialOwnerDto> getBeneficialOwners() { return beneficialOwners; }
    public void setBeneficialOwners(List<BeneficialOwnerDto> beneficialOwners) { this.beneficialOwners = beneficialOwners; }
}
