package com.bank.aml.iam.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "approval_requests")
public class ApprovalRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType; // RULE, CASE_CLOSURE, STR_FILING, CUSTOMER_OVERRIDE

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType; // CREATE, UPDATE, DELETE, CLOSE, APPROVE_SUBMISSION

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "maker_user_id", nullable = false)
    private User makerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checker_user_id")
    private User checkerUser;

    @Column(nullable = false, length = 20)
    private String status = "PENDING"; // PENDING, APPROVED, REJECTED, CANCELLED

    @Column(name = "maker_comments", columnDefinition = "TEXT")
    private String makerComments;

    @Column(name = "checker_comments", columnDefinition = "TEXT")
    private String checkerComments;

    @Column(name = "payload_snapshot", columnDefinition = "jsonb")
    private String payloadSnapshot;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    public ApprovalRequest() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public User getMakerUser() { return makerUser; }
    public void setMakerUser(User makerUser) { this.makerUser = makerUser; }
    public User getCheckerUser() { return checkerUser; }
    public void setCheckerUser(User checkerUser) { this.checkerUser = checkerUser; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMakerComments() { return makerComments; }
    public void setMakerComments(String makerComments) { this.makerComments = makerComments; }
    public String getCheckerComments() { return checkerComments; }
    public void setCheckerComments(String checkerComments) { this.checkerComments = checkerComments; }
    public String getPayloadSnapshot() { return payloadSnapshot; }
    public void setPayloadSnapshot(String payloadSnapshot) { this.payloadSnapshot = payloadSnapshot; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(Instant resolvedAt) { this.resolvedAt = resolvedAt; }
}
