package com.bank.aml.screening.repository;

import com.bank.aml.screening.model.AdverseMediaRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AdverseMediaRecordRepository extends JpaRepository<AdverseMediaRecord, UUID> {
    List<AdverseMediaRecord> findByCustomerIdOrderByPublicationDateDesc(UUID customerId);
    Page<AdverseMediaRecord> findByRelevanceStatus(String relevanceStatus, Pageable pageable);
}
