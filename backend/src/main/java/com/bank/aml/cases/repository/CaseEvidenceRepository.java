package com.bank.aml.cases.repository;

import com.bank.aml.cases.model.CaseEvidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseEvidenceRepository extends JpaRepository<CaseEvidence, UUID> {
    List<CaseEvidence> findByInvestigationCaseIdOrderByCreatedAtDesc(UUID caseId);
}
