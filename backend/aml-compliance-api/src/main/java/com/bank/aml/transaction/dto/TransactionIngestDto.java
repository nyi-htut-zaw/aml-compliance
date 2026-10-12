package com.bank.aml.transaction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionIngestDto {

    @NotBlank(message = "Reference number is required for idempotency")
    private String referenceNumber;

    @NotNull(message = "Account number or ID is required")
    private String accountNumber;

    @NotBlank(message = "Transaction type is required")
    private String transactionType; // WIRE_IN, WIRE_OUT, CASH_DEPOSIT, CASH_WITHDRAWAL, INTERNAL_TRANSFER

    @NotBlank(message = "Channel is required")
    private String channel; // SWIFT, ONLINE_BANKING, BRANCH_COUNTER, ATM, MOBILE_APP, API

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency code is required")
    private String currency; // USD, EUR, GBP

    @NotBlank(message = "Counterparty name is required")
    private String counterpartyName;

    private String counterpartyAccount;
    private String counterpartyBank;

    @NotBlank(message = "Counterparty country code is required")
    private String counterpartyCountry;

    private String purposeCode;
    private String narration;

    public TransactionIngestDto() {}

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
    public String getCounterpartyAccount() { return counterpartyAccount; }
    public void setCounterpartyAccount(String counterpartyAccount) { this.counterpartyAccount = counterpartyAccount; }
    public String getCounterpartyBank() { return counterpartyBank; }
    public void setCounterpartyBank(String counterpartyBank) { this.counterpartyBank = counterpartyBank; }
    public String getCounterpartyCountry() { return counterpartyCountry; }
    public void setCounterpartyCountry(String counterpartyCountry) { this.counterpartyCountry = counterpartyCountry; }
    public String getPurposeCode() { return purposeCode; }
    public void setPurposeCode(String purposeCode) { this.purposeCode = purposeCode; }
    public String getNarration() { return narration; }
    public void setNarration(String narration) { this.narration = narration; }
}
