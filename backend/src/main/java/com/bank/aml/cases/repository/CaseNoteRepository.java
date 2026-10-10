package com.bank.aml.cases.repository;

import com.bank.aml.cases.model.CaseNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CaseNoteRepository extends JpaRepository<CaseNote, UUID> {
    List<CaseNote> findByInvestigationCaseIdOrderByCreatedAtAsc(UUID caseId);
}
