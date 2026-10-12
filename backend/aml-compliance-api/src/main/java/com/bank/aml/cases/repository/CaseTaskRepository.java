package com.bank.aml.cases.repository;

import com.bank.aml.cases.model.CaseTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, UUID> {
    List<CaseTask> findByInvestigationCaseIdOrderByCreatedAtAsc(UUID caseId);
}
