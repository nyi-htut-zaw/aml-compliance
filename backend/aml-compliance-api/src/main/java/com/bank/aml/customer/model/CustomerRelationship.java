package com.bank.aml.customer.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "customer_relationships")
public class CustomerRelationship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_customer_id", nullable = false)
    private Customer fromCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_customer_id", nullable = false)
    private Customer toCustomer;

    @Column(name = "relationship_type", nullable = false, length = 50)
    private String relationshipType; // PARENT_COMPANY, SUBSIDIARY, DIRECTOR, SHAREHOLDER, SPOUSE, BUSINESS_PARTNER

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CustomerRelationship() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Customer getFromCustomer() { return fromCustomer; }
    public void setFromCustomer(Customer fromCustomer) { this.fromCustomer = fromCustomer; }
    public Customer getToCustomer() { return toCustomer; }
    public void setToCustomer(Customer toCustomer) { this.toCustomer = toCustomer; }
    public String getRelationshipType() { return relationshipType; }
    public void setRelationshipType(String relationshipType) { this.relationshipType = relationshipType; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
