package com.bank.aml.screening.model;

import com.bank.aml.customer.model.Customer;
import com.bank.aml.iam.model.User;
import com.bank.aml.transaction.model.Transaction;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "screening_matches")
public class ScreeningMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_run_id")
    private ScreeningRun screeningRun;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "watchlist_entry_id", nullable = false)
    private WatchlistEntry watchlistEntry;

    @Column(name = "match_type", nullable = false, length = 30)
    private String matchType; // SANCTION, PEP, ADVERSE_MEDIA

    @Column(name = "match_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal matchScore; // 0.00 to 100.00

    @Column(name = "matching_algorithm", nullable = false, length = 50)
    private String matchingAlgorithm; // JARO_WINKLER, LEVENSHTEIN, EXACT

    @Column(name = "matched_term", nullable = false)
    private String matchedTerm;

    @Column(name = "match_details_json", columnDefinition = "jsonb")
    private String matchDetailsJson;

    @Column(nullable = false, length = 30)
    private String disposition = "PENDING"; // PENDING, FALSE_POSITIVE, TRUE_POSITIVE, ESCALATED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "disposition_by_user_id")
    private User dispositionByUser;

    @Column(name = "disposition_notes", columnDefinition = "TEXT")
    private String dispositionNotes;

    @Column(name = "disposition_at")
    private Instant dispositionAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ScreeningMatch() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public ScreeningRun getScreeningRun() { return screeningRun; }
    public void setScreeningRun(ScreeningRun screeningRun) { this.screeningRun = screeningRun; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public WatchlistEntry getWatchlistEntry() { return watchlistEntry; }
    public void setWatchlistEntry(WatchlistEntry watchlistEntry) { this.watchlistEntry = watchlistEntry; }
    public String getMatchType() { return matchType; }
    public void setMatchType(String matchType) { this.matchType = matchType; }
    public BigDecimal getMatchScore() { return matchScore; }
    public void setMatchScore(BigDecimal matchScore) { this.matchScore = matchScore; }
    public String getMatchingAlgorithm() { return matchingAlgorithm; }
    public void setMatchingAlgorithm(String matchingAlgorithm) { this.matchingAlgorithm = matchingAlgorithm; }
    public String getMatchedTerm() { return matchedTerm; }
    public void setMatchedTerm(String matchedTerm) { this.matchedTerm = matchedTerm; }
    public String getMatchDetailsJson() { return matchDetailsJson; }
    public void setMatchDetailsJson(String matchDetailsJson) { this.matchDetailsJson = matchDetailsJson; }
    public String getDisposition() { return disposition; }
    public void setDisposition(String disposition) { this.disposition = disposition; }
    public User getDispositionByUser() { return dispositionByUser; }
    public void setDispositionByUser(User dispositionByUser) { this.dispositionByUser = dispositionByUser; }
    public String getDispositionNotes() { return dispositionNotes; }
    public void setDispositionNotes(String dispositionNotes) { this.dispositionNotes = dispositionNotes; }
    public Instant getDispositionAt() { return dispositionAt; }
    public void setDispositionAt(Instant dispositionAt) { this.dispositionAt = dispositionAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
