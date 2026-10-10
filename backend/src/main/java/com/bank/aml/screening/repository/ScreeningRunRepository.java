package com.bank.aml.screening.repository;

import com.bank.aml.screening.model.ScreeningRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ScreeningRunRepository extends JpaRepository<ScreeningRun, UUID> {
}
