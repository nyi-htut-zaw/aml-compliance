package com.bank.aml.iam.dto;

import java.time.Instant;
import java.util.UUID;

public class ApprovalResponseDto {
    private UUID id;
    private String entityType;
    private String entityId;
    private String actionType;
    private UUID makerUserId;
    private String makerUsername;
    private String makerFullName;
    private UUID checkerUserId;
    private String checkerUsername;
    private String checkerFullName;
    private String status;
    private String makerComments;
    private String checkerComments;
    private String payloadSnapshot;
    private Instant createdAt;
    private Instant resolvedAt;

    public ApprovalResponseDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public UUID getMakerUserId() { return makerUserId; }
    public void setMakerUserId(UUID makerUserId) { this.makerUserId = makerUserId; }
    public String getMakerUsername() { return makerUsername; }
    public void setMakerUsername(String makerUsername) { this.makerUsername = makerUsername; }
    public String getMakerFullName() { return makerFullName; }
    public void setMakerFullName(String makerFullName) { this.makerFullName = makerFullName; }
    public UUID getCheckerUserId() { return checkerUserId; }
    public void setCheckerUserId(UUID checkerUserId) { this.checkerUserId = checkerUserId; }
    public String getCheckerUsername() { return checkerUsername; }
    public void setCheckerUsername(String checkerUsername) { this.checkerUsername = checkerUsername; }
    public String getCheckerFullName() { return checkerFullName; }
    public void setCheckerFullName(String checkerFullName) { this.checkerFullName = checkerFullName; }
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
