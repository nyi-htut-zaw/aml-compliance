package com.bank.aml.audit.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "correlation_id", nullable = false, length = 100)
    private String correlationId;

    @Column(nullable = false)
    private Instant timestamp = Instant.now();

    @Column(name = "actor_username", nullable = false, length = 100)
    private String actorUsername;

    @Column(name = "actor_role", length = 100)
    private String actorRole;

    @Column(name = "client_ip", length = 50)
    private String clientIp;

    @Column(nullable = false, length = 50)
    private String module; // IAM, KYC, SCREENING, TM, ALERTS, CASES, RULES, REPORTS

    @Column(nullable = false, length = 50)
    private String action; // CREATE, UPDATE, DELETE, LOGIN, APPROVE, REJECT, DISPOSITION, EXPORT

    @Column(name = "entity_type", nullable = false, length = 50)
    private String entityType;

    @Column(name = "entity_id", nullable = false, length = 100)
    private String entityId;

    @Column(name = "before_state_json", columnDefinition = "jsonb")
    private String beforeStateJson;

    @Column(name = "after_state_json", columnDefinition = "jsonb")
    private String afterStateJson;

    @Column(columnDefinition = "TEXT")
    private String details;

    public AuditEvent() {}

    public AuditEvent(String correlationId, String actorUsername, String actorRole, String clientIp,
                      String module, String action, String entityType, String entityId, String details) {
        this.correlationId = correlationId;
        this.actorUsername = actorUsername;
        this.actorRole = actorRole;
        this.clientIp = clientIp;
        this.module = module;
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.timestamp = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    public String getActorUsername() { return actorUsername; }
    public void setActorUsername(String actorUsername) { this.actorUsername = actorUsername; }
    public String getActorRole() { return actorRole; }
    public void setActorRole(String actorRole) { this.actorRole = actorRole; }
    public String getClientIp() { return clientIp; }
    public void setClientIp(String clientIp) { this.clientIp = clientIp; }
    public String getModule() { return module; }
    public void setModule(String module) { this.module = module; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }
    public String getBeforeStateJson() { return beforeStateJson; }
    public void setBeforeStateJson(String beforeStateJson) { this.beforeStateJson = beforeStateJson; }
    public String getAfterStateJson() { return afterStateJson; }
    public void setAfterStateJson(String afterStateJson) { this.afterStateJson = afterStateJson; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
}
