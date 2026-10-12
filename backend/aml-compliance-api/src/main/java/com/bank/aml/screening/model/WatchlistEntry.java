package com.bank.aml.screening.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watchlist_entries")
public class WatchlistEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id", nullable = false)
    private WatchlistSource source;

    @Column(name = "entry_uid", nullable = false, length = 100)
    private String entryUid;

    @Column(name = "entry_type", nullable = false, length = 20)
    private String entryType; // INDIVIDUAL, ENTITY, VESSEL, AIRCRAFT

    @Column(name = "primary_name", nullable = false)
    private String primaryName;

    @Column(columnDefinition = "TEXT")
    private String aliases;

    @Column(name = "pep_category", length = 50)
    private String pepCategory; // DOMESTIC_PEP, FOREIGN_PEP, INTERNATIONAL_ORG, RCA, NONE

    @Column(name = "country_codes", length = 100)
    private String countryCodes;

    @Column(name = "date_of_birth", length = 50)
    private String dateOfBirth;

    @Column(name = "designator_programs")
    private String designatorPrograms; // SDGT, RUSSIA-EO14024, DPRK

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public WatchlistEntry() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public WatchlistSource getSource() { return source; }
    public void setSource(WatchlistSource source) { this.source = source; }
    public String getEntryUid() { return entryUid; }
    public void setEntryUid(String entryUid) { this.entryUid = entryUid; }
    public String getEntryType() { return entryType; }
    public void setEntryType(String entryType) { this.entryType = entryType; }
    public String getPrimaryName() { return primaryName; }
    public void setPrimaryName(String primaryName) { this.primaryName = primaryName; }
    public String getAliases() { return aliases; }
    public void setAliases(String aliases) { this.aliases = aliases; }
    public String getPepCategory() { return pepCategory; }
    public void setPepCategory(String pepCategory) { this.pepCategory = pepCategory; }
    public String getCountryCodes() { return countryCodes; }
    public void setCountryCodes(String countryCodes) { this.countryCodes = countryCodes; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getDesignatorPrograms() { return designatorPrograms; }
    public void setDesignatorPrograms(String designatorPrograms) { this.designatorPrograms = designatorPrograms; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
