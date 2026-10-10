package com.bank.aml.customer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public class CustomerCreateDto {

    @NotBlank(message = "Customer type is required")
    @Pattern(regexp = "INDIVIDUAL|LEGAL_ENTITY", message = "Customer type must be INDIVIDUAL or LEGAL_ENTITY")
    private String customerType;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String tradeName;

    @NotBlank(message = "Jurisdiction country code is required")
    private String jurisdictionCountry;

    @NotNull(message = "Date of birth or incorporation is required")
    private LocalDate dateOfBirthOrIncorp;

    private String taxIdentifier;

    private String sourceOfWealth;
    private String sourceOfFunds;
    private String occupationOrIndustry;
    private boolean pep = false;

    private String streetLine1;
    private String city;
    private String stateProvince;
    private String postalCode;
    private String countryCode;

    private String idType;
    private String idNumber;
    private String issuingCountry;

    public CustomerCreateDto() {}

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
    public String getSourceOfWealth() { return sourceOfWealth; }
    public void setSourceOfWealth(String sourceOfWealth) { this.sourceOfWealth = sourceOfWealth; }
    public String getSourceOfFunds() { return sourceOfFunds; }
    public void setSourceOfFunds(String sourceOfFunds) { this.sourceOfFunds = sourceOfFunds; }
    public String getOccupationOrIndustry() { return occupationOrIndustry; }
    public void setOccupationOrIndustry(String occupationOrIndustry) { this.occupationOrIndustry = occupationOrIndustry; }
    public boolean isPep() { return pep; }
    public void setPep(boolean pep) { this.pep = pep; }
    public String getStreetLine1() { return streetLine1; }
    public void setStreetLine1(String streetLine1) { this.streetLine1 = streetLine1; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getStateProvince() { return stateProvince; }
    public void setStateProvince(String stateProvince) { this.stateProvince = stateProvince; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getIdType() { return idType; }
    public void setIdType(String idType) { this.idType = idType; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public String getIssuingCountry() { return issuingCountry; }
    public void setIssuingCountry(String issuingCountry) { this.issuingCountry = issuingCountry; }
}
