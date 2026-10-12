package com.bank.aml.customer.repository;

import com.bank.aml.customer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    Optional<Customer> findByCustomerNumber(String customerNumber);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:query IS NULL OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(c.customerNumber) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:riskRating IS NULL OR c.riskRating = :riskRating) AND " +
           "(:kycStatus IS NULL OR c.kycStatus = :kycStatus) AND " +
           "(:customerType IS NULL OR c.customerType = :customerType)")
    Page<Customer> searchCustomers(
            @Param("query") String query,
            @Param("riskRating") String riskRating,
            @Param("kycStatus") String kycStatus,
            @Param("customerType") String customerType,
            Pageable pageable);

    long countByRiskRating(String riskRating);
    long countByKycStatus(String kycStatus);
    long countByPepTrue();
    long countBySanctionsHitTrue();

    @Query("SELECT c FROM Customer c WHERE c.nextKycReviewDate <= :cutoffDate AND c.accountStatus != 'EXITED'")
    List<Customer> findOverdueOrUpcomingKycReviews(@Param("cutoffDate") LocalDate cutoffDate);
}
