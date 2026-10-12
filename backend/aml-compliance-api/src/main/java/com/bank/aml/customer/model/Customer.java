package com.bank.aml.customer.model;

import com.bank.aml.iam.model.Branch;
import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Column(name = "customer_type", nullable = false, length = 20)
    private String customerType; // INDIVIDUAL, LEGAL_ENTITY

    @Column(name = "customer_number", nullable = false, unique = true, length = 50)
    private String customerNumber;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "full_name_myanmar", length = 255)
    private String fullNameMyanmar;

    @Column(name = "trade_name")
    private String tradeName;

    @Column(name = "jurisdiction_country", nullable = false, length = 3)
    private String jurisdictionCountry;

    @Column(name = "date_of_birth_or_incorp", nullable = false)
    private LocalDate dateOfBirthOrIncorp;

    @Column(name = "tax_identifier", length = 100)
    private String taxIdentifier;

    @Column(name = "risk_rating", nullable = false, length = 20)
    private String riskRating = "MEDIUM"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(name = "kyc_status", nullable = false, length = 20)
    private String kycStatus = "SUBMITTED"; // DRAFT, SUBMITTED, APPROVED, REJECTED, EXPIRED, UNDER_EDD

    @Column(name = "account_status", nullable = false, length = 20)
    private String accountStatus = "ACTIVE"; // ACTIVE, ONBOARDING, SUSPENDED, BLOCKED, EXITED

    @Column(name = "onboarding_date", nullable = false)
    private LocalDate onboardingDate = LocalDate.now();

    @Column(name = "last_kyc_review_date")
    private LocalDate lastKycReviewDate;

    @Column(name = "next_kyc_review_date", nullable = false)
    private LocalDate nextKycReviewDate = LocalDate.now().plusYears(1);

    @Column(name = "source_of_wealth", columnDefinition = "TEXT")
    private String sourceOfWealth;

    @Column(name = "source_of_funds", columnDefinition = "TEXT")
    private String sourceOfFunds;

    @Column(name = "occupation_or_industry")
    private String occupationOrIndustry;

    @Column(name = "is_pep", nullable = false)
    private boolean pep = false;

    @Column(name = "sanctions_hit", nullable = false)
    private boolean sanctionsHit = false;

    @Column(name = "adverse_media_hit", nullable = false)
    private boolean adverseMediaHit = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CustomerIdentifier> identifiers = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CustomerAddress> addresses = new ArrayList<>();

    public Customer() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }
    public String getCustomerType() { return customerType; }
    public void setCustomerType(String customerType) { this.customerType = customerType; }
    public String getCustomerNumber() { return customerNumber; }
    public void setCustomerNumber(String customerNumber) { this.customerNumber = customerNumber; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public List<CustomerIdentifier> getIdentifiers() { return identifiers; }
    public void setIdentifiers(List<CustomerIdentifier> identifiers) { this.identifiers = identifiers; }
    public List<CustomerAddress> getAddresses() { return addresses; }
    public void setAddresses(List<CustomerAddress> addresses) { this.addresses = addresses; }
}
