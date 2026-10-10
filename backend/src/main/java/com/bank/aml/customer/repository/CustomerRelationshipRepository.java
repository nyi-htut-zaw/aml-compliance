package com.bank.aml.customer.repository;

import com.bank.aml.customer.model.CustomerRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CustomerRelationshipRepository extends JpaRepository<CustomerRelationship, UUID> {
    List<CustomerRelationship> findByFromCustomerId(UUID fromCustomerId);
    List<CustomerRelationship> findByToCustomerId(UUID toCustomerId);
}
