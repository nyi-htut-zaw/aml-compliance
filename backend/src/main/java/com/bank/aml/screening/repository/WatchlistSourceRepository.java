package com.bank.aml.screening.repository;

import com.bank.aml.screening.model.WatchlistSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchlistSourceRepository extends JpaRepository<WatchlistSource, UUID> {
    Optional<WatchlistSource> findByCode(String code);
    List<WatchlistSource> findByActiveTrue();
}
