package com.bank.aml.alert.model;

import com.bank.aml.customer.model.Customer;
import com.bank.aml.iam.model.User;
import com.bank.aml.rules.model.Rule;
import com.bank.aml.transaction.model.Account;
import com.bank.aml.transaction.model.Transaction;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "alert_number", nullable = false, unique = true, length = 50)
    private String alertNumber;

    @Column(nullable = false)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id")
    private Rule rule;

    @Column(name = "trigger_event", nullable = false, length = 50)
    private String triggerEvent; // TRANSACTION, SCREENING_HIT, RISK_SPIKE, KYC_EXPIRY

    @Column(nullable = false, length = 20)
    private String severity = "HIGH"; // LOW, MEDIUM, HIGH, CRITICAL

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal score = BigDecimal.valueOf(75.0);

    @Column(nullable = false, length = 30)
    private String status = "NEW"; // NEW, ASSIGNED, UNDER_REVIEW, ESCALATED, CLOSED_FALSE_POSITIVE, CLOSED_TRUE_POSITIVE, CASE_CREATED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_user_id")
    private User assignedToUser;

    @Column(name = "sla_due_at", nullable = false)
    private Instant slaDueAt;

    @Column(name = "trigger_payload_json", columnDefinition = "jsonb")
    private String triggerPayloadJson;

    @Column(name = "disposition_notes", columnDefinition = "TEXT")
    private String dispositionNotes;

    @Column(name = "closed_at")
    private Instant closedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    public Alert() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getAlertNumber() { return alertNumber; }
    public void setAlertNumber(String alertNumber) { this.alertNumber = alertNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public Rule getRule() { return rule; }
    public void setRule(Rule rule) { this.rule = rule; }
    public String getTriggerEvent() { return triggerEvent; }
    public void setTriggerEvent(String triggerEvent) { this.triggerEvent = triggerEvent; }
    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }
    public BigDecimal getScore() { return score; }
    public void setScore(BigDecimal score) { this.score = score; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public User getAssignedToUser() { return assignedToUser; }
    public void setAssignedToUser(User assignedToUser) { this.assignedToUser = assignedToUser; }
    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public String getTriggerPayloadJson() { return triggerPayloadJson; }
    public void setTriggerPayloadJson(String triggerPayloadJson) { this.triggerPayloadJson = triggerPayloadJson; }
    public String getDispositionNotes() { return dispositionNotes; }
    public void setDispositionNotes(String dispositionNotes) { this.dispositionNotes = dispositionNotes; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
