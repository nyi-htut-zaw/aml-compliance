package com.bank.aml.cases.model;

import com.bank.aml.iam.model.User;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "case_notes")
public class CaseNote {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case investigationCase;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(name = "note_text", columnDefinition = "TEXT", nullable = false)
    private String noteText;

    @Column(name = "is_internal_only", nullable = false)
    private boolean internalOnly = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CaseNote() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Case getInvestigationCase() { return investigationCase; }
    public void setInvestigationCase(Case investigationCase) { this.investigationCase = investigationCase; }
    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }
    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }
    public boolean isInternalOnly() { return internalOnly; }
    public void setInternalOnly(boolean internalOnly) { this.internalOnly = internalOnly; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
