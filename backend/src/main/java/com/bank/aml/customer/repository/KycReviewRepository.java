package com.bank.aml.customer.repository;

import com.bank.aml.customer.model.KycReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface KycReviewRepository extends JpaRepository<KycReview, UUID> {
    List<KycReview> findByCustomerIdOrderByReviewDateDesc(UUID customerId);
    Page<KycReview> findByStatus(String status, Pageable pageable);
    long countByStatus(String status);
}
