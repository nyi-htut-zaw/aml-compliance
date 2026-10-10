package com.bank.aml.cases.model;

import com.bank.aml.customer.model.DocumentMetadata;
import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "case_evidence")
public class CaseEvidence {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case investigationCase;

    @Column(name = "evidence_type", nullable = false, length = 50)
    private String evidenceType; // TRANSACTION_FLOW, SCREENING_HIT, DOCUMENT, ADVERSE_MEDIA, EXTERNAL_REPORT

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "source_attribution", nullable = false)
    private String sourceAttribution;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_metadata_id")
    private DocumentMetadata documentMetadata;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recorded_by", nullable = false)
    private User recordedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CaseEvidence() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Case getInvestigationCase() { return investigationCase; }
    public void setInvestigationCase(Case investigationCase) { this.investigationCase = investigationCase; }
    public String getEvidenceType() { return evidenceType; }
    public void setEvidenceType(String evidenceType) { this.evidenceType = evidenceType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getSourceAttribution() { return sourceAttribution; }
    public void setSourceAttribution(String sourceAttribution) { this.sourceAttribution = sourceAttribution; }
    public DocumentMetadata getDocumentMetadata() { return documentMetadata; }
    public void setDocumentMetadata(DocumentMetadata documentMetadata) { this.documentMetadata = documentMetadata; }
    public User getRecordedBy() { return recordedBy; }
    public void setRecordedBy(User recordedBy) { this.recordedBy = recordedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
