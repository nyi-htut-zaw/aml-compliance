package com.bank.aml.screening.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "watchlist_sources")
public class WatchlistSource {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String code; // OFAC_SDN_SYNTHETIC, UN_SANCTIONS_SYNTHETIC, PEP_GLOBAL_SYNTHETIC

    @Column(name = "list_type", nullable = false, length = 50)
    private String listType; // SANCTIONS, PEP, ADVERSE_MEDIA

    @Column(nullable = false, length = 50)
    private String version;

    @Column(nullable = false, length = 100)
    private String provider;

    @Column(name = "item_count", nullable = false)
    private int itemCount = 0;

    @Column(name = "last_updated_at", nullable = false)
    private Instant lastUpdatedAt = Instant.now();

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    public WatchlistSource() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getListType() { return listType; }
    public void setListType(String listType) { this.listType = listType; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public int getItemCount() { return itemCount; }
    public void setItemCount(int itemCount) { this.itemCount = itemCount; }
    public Instant getLastUpdatedAt() { return lastUpdatedAt; }
    public void setLastUpdatedAt(Instant lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
