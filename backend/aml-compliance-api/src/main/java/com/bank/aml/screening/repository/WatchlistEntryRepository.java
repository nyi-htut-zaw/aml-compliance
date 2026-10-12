package com.bank.aml.screening.repository;

import com.bank.aml.screening.model.WatchlistEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WatchlistEntryRepository extends JpaRepository<WatchlistEntry, UUID> {
    List<WatchlistEntry> findBySourceActiveTrue();

    @Query("SELECT e FROM WatchlistEntry e WHERE " +
           "(:query IS NULL OR LOWER(e.primaryName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(e.aliases) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:sourceCode IS NULL OR e.source.code = :sourceCode)")
    Page<WatchlistEntry> searchEntries(@Param("query") String query, @Param("sourceCode") String sourceCode, Pageable pageable);
}
