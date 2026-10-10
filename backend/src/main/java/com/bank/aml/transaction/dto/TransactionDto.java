package com.bank.aml.transaction.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class TransactionDto {
    private UUID id;
    private String referenceNumber;
    private UUID accountId;
    private String accountNumber;
    private UUID customerId;
    private String customerName;
    private String transactionType;
    private String channel;
    private BigDecimal amount;
    private String currency;
    private BigDecimal baseAmountUsd;
    private String counterpartyName;
    private String counterpartyAccount;
    private String counterpartyBank;
    private String counterpartyCountry;
    private Instant bookingDate;
    private String purposeCode;
    private String narration;
    private String status;

    public TransactionDto() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public UUID getAccountId() { return accountId; }
    public void setAccountId(UUID accountId) { this.accountId = accountId; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public UUID getCustomerId() { return customerId; }
    public void setCustomerId(UUID customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
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
    public String getPurposeCode() { return purposeCode; }
    public void setPurposeCode(String purposeCode) { this.purposeCode = purposeCode; }
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
