package com.bank.aml.transaction.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.rules.service.RuleEngineService;
import com.bank.aml.transaction.dto.BatchImportResultDto;
import com.bank.aml.transaction.dto.TransactionDto;
import com.bank.aml.transaction.dto.TransactionIngestDto;
import com.bank.aml.transaction.model.Account;
import com.bank.aml.transaction.model.Transaction;
import com.bank.aml.transaction.repository.AccountRepository;
import com.bank.aml.transaction.repository.TransactionRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final RuleEngineService ruleEngineService;
    private final AuditService auditService;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository,
            RuleEngineService ruleEngineService,
            AuditService auditService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.ruleEngineService = ruleEngineService;
        this.auditService = auditService;
    }

    @Transactional
    public TransactionDto ingestTransaction(TransactionIngestDto dto) {
        // Idempotency check
        if (transactionRepository.existsByReferenceNumber(dto.getReferenceNumber())) {
            throw new BusinessRuleException("Duplicate transaction reference '" + dto.getReferenceNumber() + "' rejected. Idempotency enforced.");
        }

        Account account = accountRepository.findByAccountNumber(dto.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", dto.getAccountNumber()));

        BigDecimal baseUsd = convertToBaseUsd(dto.getAmount(), dto.getCurrency());

        Transaction txn = new Transaction();
        txn.setReferenceNumber(dto.getReferenceNumber());
        txn.setAccount(account);
        txn.setCustomer(account.getCustomer());
        txn.setTransactionType(dto.getTransactionType());
        txn.setChannel(dto.getChannel());
        txn.setAmount(dto.getAmount());
        txn.setCurrency(dto.getCurrency().toUpperCase());
        txn.setBaseAmountUsd(baseUsd);
        txn.setCounterpartyName(dto.getCounterpartyName());
        txn.setCounterpartyAccount(dto.getCounterpartyAccount());
        txn.setCounterpartyBank(dto.getCounterpartyBank());
        txn.setCounterpartyCountry(dto.getCounterpartyCountry().toUpperCase());
        txn.setPurposeCode(dto.getPurposeCode());
        txn.setNarration(dto.getNarration());
        txn.setStatus("SETTLED");
        txn.setBookingDate(Instant.now());
        txn.setValueDate(Instant.now());

        Transaction saved = transactionRepository.save(txn);

        // Update Account Activity and balance
        if (dto.getTransactionType().contains("IN") || dto.getTransactionType().contains("DEPOSIT")) {
            account.setBalance(account.getBalance().add(dto.getAmount()));
        } else if (dto.getTransactionType().contains("OUT") || dto.getTransactionType().contains("WITHDRAWAL")) {
            account.setBalance(account.getBalance().subtract(dto.getAmount()));
        }
        account.setLastActivityAt(Instant.now());
        accountRepository.save(account);

        // Evaluate AML Rules in real-time
        ruleEngineService.evaluateTransaction(saved);

        auditService.recordEvent("TM", "INGEST_TRANSACTION", "TRANSACTION", saved.getId().toString(),
                "Ingested txn " + saved.getReferenceNumber() + " for " + saved.getAmount() + " " + saved.getCurrency());

        return mapToDto(saved);
    }

    @Transactional
    public BatchImportResultDto ingestCsv(InputStream inputStream) {
        BatchImportResultDto result = new BatchImportResultDto();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setIgnoreHeaderCase(true).setTrim(true).build())) {

            for (CSVRecord record : csvParser) {
                result.setTotalRows(result.getTotalRows() + 1);
                String ref = record.get("referenceNumber");

                if (transactionRepository.existsByReferenceNumber(ref)) {
                    result.setDuplicatesSkipped(result.getDuplicatesSkipped() + 1);
                    continue;
                }

                try {
                    TransactionIngestDto dto = new TransactionIngestDto();
                    dto.setReferenceNumber(ref);
                    dto.setAccountNumber(record.get("accountNumber"));
                    dto.setTransactionType(record.get("transactionType"));
                    dto.setChannel(record.get("channel"));
                    dto.setAmount(new BigDecimal(record.get("amount")));
                    dto.setCurrency(record.get("currency"));
                    dto.setCounterpartyName(record.get("counterpartyName"));
                    dto.setCounterpartyCountry(record.get("counterpartyCountry"));
                    if (record.isMapped("counterpartyAccount")) dto.setCounterpartyAccount(record.get("counterpartyAccount"));
                    if (record.isMapped("purposeCode")) dto.setPurposeCode(record.get("purposeCode"));
                    if (record.isMapped("narration")) dto.setNarration(record.get("narration"));

                    ingestTransaction(dto);
                    result.setProcessedCount(result.getProcessedCount() + 1);
                } catch (Exception ex) {
                    result.getErrors().add("Row " + record.getRecordNumber() + " (" + ref + "): " + ex.getMessage());
                }
            }

        } catch (Exception ex) {
            result.getErrors().add("CSV parse error: " + ex.getMessage());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Page<TransactionDto> searchTransactions(UUID customerId, UUID accountId, String status, Pageable pageable) {
        return transactionRepository.searchTransactions(customerId, accountId, status, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<TransactionDto> getTransactionsForCustomer(UUID customerId) {
        return transactionRepository.findByCustomerIdOrderByBookingDateDesc(customerId).stream()
                .map(this::mapToDto)
                .toList();
    }

    private BigDecimal convertToBaseUsd(BigDecimal amount, String currency) {
        return switch (currency.toUpperCase()) {
            case "EUR" -> amount.multiply(BigDecimal.valueOf(1.09)).setScale(2, RoundingMode.HALF_UP);
            case "GBP" -> amount.multiply(BigDecimal.valueOf(1.28)).setScale(2, RoundingMode.HALF_UP);
            default -> amount.setScale(2, RoundingMode.HALF_UP);
        };
    }

    public TransactionDto mapToDto(Transaction txn) {
        TransactionDto dto = new TransactionDto();
        dto.setId(txn.getId());
        dto.setReferenceNumber(txn.getReferenceNumber());
        dto.setAccountId(txn.getAccount().getId());
        dto.setAccountNumber(txn.getAccount().getAccountNumber());
        dto.setCustomerId(txn.getCustomer().getId());
        dto.setCustomerName(txn.getCustomer().getFullName());
        dto.setTransactionType(txn.getTransactionType());
        dto.setChannel(txn.getChannel());
        dto.setAmount(txn.getAmount());
        dto.setCurrency(txn.getCurrency());
        dto.setBaseAmountUsd(txn.getBaseAmountUsd());
        dto.setCounterpartyName(txn.getCounterpartyName());
        dto.setCounterpartyAccount(txn.getCounterpartyAccount());
        dto.setCounterpartyBank(txn.getCounterpartyBank());
        dto.setCounterpartyCountry(txn.getCounterpartyCountry());
        dto.setBookingDate(txn.getBookingDate());
        dto.setPurposeCode(txn.getPurposeCode());
        dto.setNarration(txn.getNarration());
        dto.setStatus(txn.getStatus());
        return dto;
    }
}
