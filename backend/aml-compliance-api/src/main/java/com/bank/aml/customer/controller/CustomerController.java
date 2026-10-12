package com.bank.aml.customer.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import com.bank.aml.customer.dto.CustomerCreateDto;
import com.bank.aml.customer.dto.CustomerDetailDto;
import com.bank.aml.customer.dto.CustomerDto;
import com.bank.aml.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/customers", "/customers"})
@Tag(name = "Customer 360 & KYC", description = "Customer onboarding, profiles, identifiers, and due diligence")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Search and filter customers with risk rating and KYC status")
    public ApiResponse<PagedResponse<CustomerDto>> searchCustomers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String riskRating,
            @RequestParam(required = false) String kycStatus,
            @RequestParam(required = false) String customerType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<CustomerDto> result = customerService.searchCustomers(
                query, riskRating, kycStatus, customerType,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
        return ApiResponse.ok(PagedResponse.of(result));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Retrieve complete Customer 360 profile including UBOs, identifiers and addresses")
    public ApiResponse<CustomerDetailDto> getCustomer(@PathVariable UUID id) {
        CustomerDetailDto customer = customerService.getCustomerDetail(id);
        return ApiResponse.ok(customer);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'ANALYST')")
    @Operation(summary = "Onboard new Individual or Legal Entity customer")
    public ApiResponse<CustomerDto> createCustomer(@Valid @RequestBody CustomerCreateDto createDto) {
        CustomerDto customer = customerService.createCustomer(createDto);
        return ApiResponse.ok("Customer onboarded successfully", customer);
    }
}
