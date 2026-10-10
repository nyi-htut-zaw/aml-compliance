package com.bank.aml.customer.repository;

import com.bank.aml.customer.model.DocumentMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DocumentMetadataRepository extends JpaRepository<DocumentMetadata, UUID> {
    List<DocumentMetadata> findByEntityTypeAndEntityId(String entityType, String entityId);
}
