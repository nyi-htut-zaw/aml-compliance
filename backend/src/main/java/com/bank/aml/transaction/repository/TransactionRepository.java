package com.bank.aml.transaction.repository;

import com.bank.aml.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReferenceNumber(String referenceNumber);
    boolean existsByReferenceNumber(String referenceNumber);

    List<Transaction> findByCustomerIdOrderByBookingDateDesc(UUID customerId);

    @Query("SELECT t FROM Transaction t WHERE t.customer.id = :customerId AND t.bookingDate >= :since ORDER BY t.bookingDate DESC")
    List<Transaction> findRecentTransactionsByCustomer(@Param("customerId") UUID customerId, @Param("since") Instant since);

    @Query("SELECT t FROM Transaction t WHERE " +
           "(:customerId IS NULL OR t.customer.id = :customerId) AND " +
           "(:accountId IS NULL OR t.account.id = :accountId) AND " +
           "(:status IS NULL OR t.status = :status)")
    Page<Transaction> searchTransactions(
            @Param("customerId") UUID customerId,
            @Param("accountId") UUID accountId,
            @Param("status") String status,
            Pageable pageable);
}
