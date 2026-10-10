package com.bank.aml.customer.model;

import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "kyc_reviews")
public class KycReview {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "review_type", nullable = false, length = 30)
    private String reviewType; // ONBOARDING, PERIODIC_ANNUAL, EVENT_DRIVEN, EDD_ESCALATION

    @Column(name = "due_diligence_level", nullable = false, length = 20)
    private String dueDiligenceLevel; // SIMPLIFIED, STANDARD, ENHANCED

    @Column(nullable = false, length = 20)
    private String status = "IN_PROGRESS"; // SCHEDULED, IN_PROGRESS, APPROVED, REJECTED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    private User reviewer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(name = "risk_score", precision = 5, scale = 2)
    private BigDecimal riskScore;

    @Column(columnDefinition = "TEXT")
    private String findings;

    @Column(name = "decision_notes", columnDefinition = "TEXT")
    private String decisionNotes;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate = LocalDate.now();

    @Column(name = "next_review_date", nullable = false)
    private LocalDate nextReviewDate = LocalDate.now().plusYears(1);

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public KycReview() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getReviewType() { return reviewType; }
    public void setReviewType(String reviewType) { this.reviewType = reviewType; }
    public String getDueDiligenceLevel() { return dueDiligenceLevel; }
    public void setDueDiligenceLevel(String dueDiligenceLevel) { this.dueDiligenceLevel = dueDiligenceLevel; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getReviewer() { return reviewer; }
    public void setReviewer(User reviewer) { this.reviewer = reviewer; }
    public User getApprover() { return approver; }
    public void setApprover(User approver) { this.approver = approver; }
    public BigDecimal getRiskScore() { return riskScore; }
    public void setRiskScore(BigDecimal riskScore) { this.riskScore = riskScore; }
    public String getFindings() { return findings; }
    public void setFindings(String findings) { this.findings = findings; }
    public String getDecisionNotes() { return decisionNotes; }
    public void setDecisionNotes(String decisionNotes) { this.decisionNotes = decisionNotes; }
    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }
    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
