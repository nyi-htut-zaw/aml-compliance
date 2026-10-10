package com.bank.aml.transaction.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import com.bank.aml.transaction.dto.BatchImportResultDto;
import com.bank.aml.transaction.dto.TransactionDto;
import com.bank.aml.transaction.dto.TransactionIngestDto;
import com.bank.aml.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/transactions", "/transactions"})
@Tag(name = "Transaction Monitoring", description = "Transaction ingestion, deduplication, batch CSV imports, and event timeline")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Search financial transactions with pagination and status filters")
    public ApiResponse<PagedResponse<TransactionDto>> getTransactions(
            @RequestParam(required = false) UUID customerId,
            @RequestParam(required = false) UUID accountId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<TransactionDto> result = transactionService.searchTransactions(
                customerId, accountId, status,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "bookingDate"))
        );
        return ApiResponse.ok(PagedResponse.of(result));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Get transaction history for customer timeline and flow-of-funds analysis")
    public ApiResponse<List<TransactionDto>> getCustomerTransactions(@PathVariable UUID customerId) {
        List<TransactionDto> list = transactionService.getTransactionsForCustomer(customerId);
        return ApiResponse.ok(list);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'ANALYST')")
    @Operation(summary = "Ingest single transaction via API with idempotency and real-time scenario surveillance")
    public ApiResponse<TransactionDto> ingestTransaction(@Valid @RequestBody TransactionIngestDto ingestDto) {
        TransactionDto result = transactionService.ingestTransaction(ingestDto);
        return ApiResponse.ok("Transaction ingested successfully", result);
    }

    @PostMapping(value = "/upload-csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'ANALYST')")
    @Operation(summary = "Batch ingest transactions from CSV file with validation and deduplication report")
    public ApiResponse<BatchImportResultDto> uploadCsv(@RequestParam("file") MultipartFile file) throws IOException {
        BatchImportResultDto report = transactionService.ingestCsv(file.getInputStream());
        return ApiResponse.ok("Batch CSV import processed", report);
    }
}
