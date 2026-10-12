package com.bank.aml.iam.repository;

import com.bank.aml.iam.model.ApprovalRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApprovalRequestRepository extends JpaRepository<ApprovalRequest, UUID> {
    List<ApprovalRequest> findByStatusOrderByCreatedAtDesc(String status);
    Page<ApprovalRequest> findByStatus(String status, Pageable pageable);
    Optional<ApprovalRequest> findByEntityTypeAndEntityIdAndStatus(String entityType, String entityId, String status);
    long countByStatus(String status);
}
