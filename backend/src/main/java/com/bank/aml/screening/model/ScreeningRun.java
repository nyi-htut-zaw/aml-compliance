package com.bank.aml.screening.model;

import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "screening_runs")
public class ScreeningRun {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // CUSTOMER, TRANSACTION, COUNTERPARTY

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "run_type", nullable = false, length = 30)
    private String runType; // REAL_TIME, ON_DEMAND_BATCH, RE_SCREENING

    @Column(name = "total_candidates", nullable = false)
    private int totalCandidates = 0;

    @Column(name = "matches_found", nullable = false)
    private int matchesFound = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "run_by_user_id")
    private User runByUser;

    @Column(name = "completed_at", nullable = false)
    private Instant completedAt = Instant.now();

    public ScreeningRun() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getRunType() { return runType; }
    public void setRunType(String runType) { this.runType = runType; }
    public int getTotalCandidates() { return totalCandidates; }
    public void setTotalCandidates(int totalCandidates) { this.totalCandidates = totalCandidates; }
    public int getMatchesFound() { return matchesFound; }
    public void setMatchesFound(int matchesFound) { this.matchesFound = matchesFound; }
    public User getRunByUser() { return runByUser; }
    public void setRunByUser(User runByUser) { this.runByUser = runByUser; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
