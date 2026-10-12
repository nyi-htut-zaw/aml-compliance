package com.bank.aml.customer.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "beneficial_owners")
public class BeneficialOwner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entity_customer_id", nullable = false)
    private Customer entityCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_customer_id")
    private Customer ownerCustomer;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(length = 3)
    private String nationality;

    @Column(name = "ownership_percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal ownershipPercentage;

    @Column(name = "control_type", nullable = false, length = 50)
    private String controlType; // DIRECT_SHAREHOLDER, INDIRECT_SHAREHOLDER, VOTING_RIGHTS, SENIOR_MANAGING_OFFICIAL, POWER_OF_ATTORNEY

    @Column(name = "is_ubo", nullable = false)
    private boolean ubo = false; // Evaluated against threshold (e.g. >= 25%)

    @Column(name = "verification_status", nullable = false, length = 20)
    private String verificationStatus = "VERIFIED";

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom = LocalDate.now();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public BeneficialOwner() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Customer getEntityCustomer() { return entityCustomer; }
    public void setEntityCustomer(Customer entityCustomer) { this.entityCustomer = entityCustomer; }
    public Customer getOwnerCustomer() { return ownerCustomer; }
    public void setOwnerCustomer(Customer ownerCustomer) { this.ownerCustomer = ownerCustomer; }
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
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
