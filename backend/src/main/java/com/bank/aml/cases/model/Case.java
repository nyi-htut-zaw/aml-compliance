package com.bank.aml.cases.model;

import com.bank.aml.alert.model.Alert;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.iam.model.User;
import com.bank.aml.transaction.model.Transaction;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cases")
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "case_number", nullable = false, unique = true, length = 50)
    private String caseNumber;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(nullable = false, length = 20)
    private String priority = "HIGH"; // LOW, MEDIUM, HIGH, URGENT

    @Column(nullable = false, length = 30)
    private String status = "IN_INVESTIGATION"; // NEW, ASSIGNED, IN_INVESTIGATION, PENDING_APPROVAL, ESCALATED_MLRO, CLOSED_CLEARED, CLOSED_SUSPICIOUS, FILED_STR

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "primary_analyst_id")
    private User primaryAnalyst;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id")
    private User approver;

    @Column(name = "sla_due_at", nullable = false)
    private Instant slaDueAt;

    @Column(name = "investigation_summary", columnDefinition = "TEXT")
    private String investigationSummary;

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "closure_decision", length = 50)
    private String closureDecision;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "closed_at")
    private Instant closedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "case_alerts",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "alert_id")
    )
    private List<Alert> linkedAlerts = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "case_transactions",
        joinColumns = @JoinColumn(name = "case_id"),
        inverseJoinColumns = @JoinColumn(name = "transaction_id")
    )
    private List<Transaction> linkedTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CaseEvidence> evidenceList = new ArrayList<>();

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CaseTask> tasks = new ArrayList<>();

    @OneToMany(mappedBy = "investigationCase", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CaseNote> notes = new ArrayList<>();

    public Case() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCaseNumber() { return caseNumber; }
    public void setCaseNumber(String caseNumber) { this.caseNumber = caseNumber; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public User getPrimaryAnalyst() { return primaryAnalyst; }
    public void setPrimaryAnalyst(User primaryAnalyst) { this.primaryAnalyst = primaryAnalyst; }
    public User getApprover() { return approver; }
    public void setApprover(User approver) { this.approver = approver; }
    public Instant getSlaDueAt() { return slaDueAt; }
    public void setSlaDueAt(Instant slaDueAt) { this.slaDueAt = slaDueAt; }
    public String getInvestigationSummary() { return investigationSummary; }
    public void setInvestigationSummary(String investigationSummary) { this.investigationSummary = investigationSummary; }
    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }
    public String getClosureDecision() { return closureDecision; }
    public void setClosureDecision(String closureDecision) { this.closureDecision = closureDecision; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }
    public List<Alert> getLinkedAlerts() { return linkedAlerts; }
    public void setLinkedAlerts(List<Alert> linkedAlerts) { this.linkedAlerts = linkedAlerts; }
    public List<Transaction> getLinkedTransactions() { return linkedTransactions; }
    public void setLinkedTransactions(List<Transaction> linkedTransactions) { this.linkedTransactions = linkedTransactions; }
    public List<CaseEvidence> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<CaseEvidence> evidenceList) { this.evidenceList = evidenceList; }
    public List<CaseTask> getTasks() { return tasks; }
    public void setTasks(List<CaseTask> tasks) { this.tasks = tasks; }
    public List<CaseNote> getNotes() { return notes; }
    public void setNotes(List<CaseNote> notes) { this.notes = notes; }
}
