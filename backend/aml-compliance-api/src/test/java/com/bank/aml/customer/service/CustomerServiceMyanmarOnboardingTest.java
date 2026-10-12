package com.bank.aml.customer.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.customer.dto.CustomerCreateDto;
import com.bank.aml.customer.dto.CustomerDto;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.customer.repository.BeneficialOwnerRepository;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.iam.model.Branch;
import com.bank.aml.iam.repository.BranchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerServiceMyanmarOnboardingTest {

    private CustomerRepository customerRepository;
    private BranchRepository branchRepository;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        branchRepository = mock(BranchRepository.class);
        Branch branch = new Branch();
        branch.setCode("BR-NYC");
        when(branchRepository.findByCode("BR-NYC")).thenReturn(Optional.of(branch));
        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer customer = invocation.getArgument(0);
                    customer.setId(UUID.randomUUID());
                    return customer;
                });

        customerService = new CustomerService(
                customerRepository,
                branchRepository,
                mock(BeneficialOwnerRepository.class),
                mock(AuditService.class),
                mock(BeneficialOwnershipService.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"MMR", "RUS"})
    void jurisdictionDoesNotAutomaticallyIncreaseCustomerRisk(String jurisdiction) {
        CustomerCreateDto request = customerRequest();
        request.setJurisdictionCountry(jurisdiction);

        CustomerDto result = customerService.createCustomer(request);

        assertEquals("MEDIUM", result.getRiskRating());
        assertEquals("SUBMITTED", result.getKycStatus());
    }

    @Test
    void myanmarScriptNameIsPersistedAndPepRequiresEnhancedReview() {
        CustomerCreateDto request = customerRequest();
        request.setFullNameMyanmar("မောင်မောင်");
        request.setPep(true);

        CustomerDto result = customerService.createCustomer(request);

        assertEquals("မောင်မောင်", result.getFullNameMyanmar());
        assertEquals("HIGH", result.getRiskRating());
        assertEquals("UNDER_EDD", result.getKycStatus());
    }

    private CustomerCreateDto customerRequest() {
        CustomerCreateDto request = new CustomerCreateDto();
        request.setCustomerType("INDIVIDUAL");
        request.setFullName("Example Customer");
        request.setJurisdictionCountry("MMR");
        request.setDateOfBirthOrIncorp(LocalDate.of(1990, 1, 1));
        return request;
    }
}
