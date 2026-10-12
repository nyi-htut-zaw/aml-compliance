package com.bank.aml.transaction.model;

import com.bank.aml.customer.model.Customer;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "reference_number", nullable = false, unique = true, length = 100)
    private String referenceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "transaction_type", nullable = false, length = 50)
    private String transactionType; // WIRE_IN, WIRE_OUT, CASH_DEPOSIT, CASH_WITHDRAWAL, INTERNAL_TRANSFER

    @Column(nullable = false, length = 50)
    private String channel; // SWIFT, ONLINE_BANKING, BRANCH_COUNTER, ATM, MOBILE_APP, API

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(name = "base_amount_usd", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseAmountUsd;

    @Column(name = "counterparty_name", nullable = false)
    private String counterpartyName;

    @Column(name = "counterparty_account", length = 100)
    private String counterpartyAccount;

    @Column(name = "counterparty_bank")
    private String counterpartyBank;

    @Column(name = "counterparty_country", nullable = false, length = 3)
    private String counterpartyCountry;

    @Column(name = "booking_date", nullable = false)
    private Instant bookingDate = Instant.now();

    @Column(name = "value_date", nullable = false)
    private Instant valueDate = Instant.now();

    @Column(name = "purpose_code", length = 50)
    private String purposeCode;

    @Column(columnDefinition = "TEXT")
    private String narration;

    @Column(nullable = false, length = 20)
    private String status = "SETTLED"; // SETTLED, PENDING, HELD_FOR_REVIEW, BLOCKED, REJECTED

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Transaction() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public Account getAccount() { return account; }
    public void setAccount(Account account) { this.account = account; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public BigDecimal getBaseAmountUsd() { return baseAmountUsd; }
    public void setBaseAmountUsd(BigDecimal baseAmountUsd) { this.baseAmountUsd = baseAmountUsd; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }
    public String getCounterpartyBank() { return counterpartyBank; }
    public void setCounterpartyBank(String counterpartyBank) { this.counterpartyBank = counterpartyBank; }
    public String getCounterpartyCountry() { return counterpartyCountry; }
    public void setCounterpartyCountry(String counterpartyCountry) { this.counterpartyCountry = counterpartyCountry; }
    public Instant getBookingDate() { return bookingDate; }
    public void setBookingDate(Instant bookingDate) { this.bookingDate = bookingDate; }
    public Instant getValueDate() { return valueDate; }
    public void setValueDate(Instant valueDate) { this.valueDate = valueDate; }
    public String getPurposeCode() { return purposeCode; }
    public void setPurposeCode(String purposeCode) { this.purposeCode = purposeCode; }
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
