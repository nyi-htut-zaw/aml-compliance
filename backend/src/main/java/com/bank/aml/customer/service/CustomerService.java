package com.bank.aml.customer.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.BusinessRuleException;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.customer.dto.CustomerCreateDto;
import com.bank.aml.customer.dto.CustomerDetailDto;
import com.bank.aml.customer.dto.CustomerDto;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.customer.model.CustomerAddress;
import com.bank.aml.customer.model.CustomerIdentifier;
import com.bank.aml.customer.repository.BeneficialOwnerRepository;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.iam.model.Branch;
import com.bank.aml.iam.repository.BranchRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final BeneficialOwnerRepository beneficialOwnerRepository;
    private final AuditService auditService;
    private final BeneficialOwnershipService beneficialOwnershipService;

    public CustomerService(
            CustomerRepository customerRepository,
            BranchRepository branchRepository,
            BeneficialOwnerRepository beneficialOwnerRepository,
            AuditService auditService,
            BeneficialOwnershipService beneficialOwnershipService) {
        this.customerRepository = customerRepository;
        this.branchRepository = branchRepository;
        this.beneficialOwnerRepository = beneficialOwnerRepository;
        this.auditService = auditService;
        this.beneficialOwnershipService = beneficialOwnershipService;
    }

    @Transactional(readOnly = true)
    public Page<CustomerDto> searchCustomers(String query, String riskRating, String kycStatus,
                                             String customerType, Pageable pageable) {
        return customerRepository.searchCustomers(query, riskRating, kycStatus, customerType, pageable)
                .map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public CustomerDetailDto getCustomerDetail(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        CustomerDetailDto dto = new CustomerDetailDto();
        dto.setId(customer.getId());
        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setCustomerType(customer.getCustomerType());
        dto.setFullName(customer.getFullName());
        dto.setTradeName(customer.getTradeName());
        dto.setJurisdictionCountry(customer.getJurisdictionCountry());
        dto.setDateOfBirthOrIncorp(customer.getDateOfBirthOrIncorp());
        dto.setTaxIdentifier(customer.getTaxIdentifier());
        dto.setRiskRating(customer.getRiskRating());
        dto.setKycStatus(customer.getKycStatus());
        dto.setAccountStatus(customer.getAccountStatus());
        dto.setOnboardingDate(customer.getOnboardingDate());
        dto.setLastKycReviewDate(customer.getLastKycReviewDate());
        dto.setNextKycReviewDate(customer.getNextKycReviewDate());
        dto.setSourceOfWealth(customer.getSourceOfWealth());
        dto.setSourceOfFunds(customer.getSourceOfFunds());
        dto.setOccupationOrIndustry(customer.getOccupationOrIndustry());
        dto.setPep(customer.isPep());
        dto.setSanctionsHit(customer.isSanctionsHit());
        dto.setAdverseMediaHit(customer.isAdverseMediaHit());

        if (customer.getBranch() != null) {
            dto.setBranchName(customer.getBranch().getName());
            dto.setBranchCode(customer.getBranch().getCode());
        }

        dto.setIdentifiers(customer.getIdentifiers());
        dto.setAddresses(customer.getAddresses());
        dto.setBeneficialOwners(beneficialOwnershipService.getBeneficialOwnersForEntity(customer.getId()));

        return dto;
    }

    @Transactional
    public CustomerDto createCustomer(CustomerCreateDto createDto) {
        UUID branchId = SecurityUtils.getCurrentBranchId().orElse(null);
        Branch branch = null;
        if (branchId != null) {
            branch = branchRepository.findById(branchId).orElse(null);
        }
        if (branch == null) {
            branch = branchRepository.findByCode("BR-NYC")
                    .orElseThrow(() -> new BusinessRuleException("Default branch not configured"));
        }

        Customer customer = new Customer();
        customer.setBranch(branch);
        customer.setCustomerType(createDto.getCustomerType());
        customer.setCustomerNumber("CUST-GTB-" + ThreadLocalRandom.current().nextInt(2000, 9999));
        customer.setFullName(createDto.getFullName());
        customer.setTradeName(createDto.getTradeName());
        customer.setJurisdictionCountry(createDto.getJurisdictionCountry().toUpperCase());
        customer.setDateOfBirthOrIncorp(createDto.getDateOfBirthOrIncorp());
        customer.setTaxIdentifier(createDto.getTaxIdentifier());
        customer.setSourceOfWealth(createDto.getSourceOfWealth());
        customer.setSourceOfFunds(createDto.getSourceOfFunds());
        customer.setOccupationOrIndustry(createDto.getOccupationOrIndustry());
        customer.setPep(createDto.isPep());

        // Initial risk rating assessment
        if (createDto.isPep() || List.of("IRN", "PRK", "RUS", "SYR", "CYP").contains(createDto.getJurisdictionCountry().toUpperCase())) {
            customer.setRiskRating("HIGH");
            customer.setKycStatus("UNDER_EDD");
        } else {
            customer.setRiskRating("MEDIUM");
            customer.setKycStatus("SUBMITTED");
        }

        customer.setAccountStatus("ONBOARDING");
        customer.setOnboardingDate(LocalDate.now());
        customer.setNextKycReviewDate(LocalDate.now().plusMonths("HIGH".equalsIgnoreCase(customer.getRiskRating()) ? 6 : 12));

        // Primary Address
        if (createDto.getStreetLine1() != null && !createDto.getStreetLine1().isBlank()) {
            CustomerAddress address = new CustomerAddress();
            address.setCustomer(customer);
            address.setAddressType("INDIVIDUAL".equalsIgnoreCase(customer.getCustomerType()) ? "RESIDENTIAL" : "REGISTERED");
            address.setStreetLine1(createDto.getStreetLine1());
            address.setCity(createDto.getCity() != null ? createDto.getCity() : "City");
            address.setStateProvince(createDto.getStateProvince());
            address.setPostalCode(createDto.getPostalCode());
            address.setCountryCode(createDto.getCountryCode() != null ? createDto.getCountryCode().toUpperCase() : customer.getJurisdictionCountry());
            address.setCurrent(true);
            customer.getAddresses().add(address);
        }

        // Primary Identifier
        if (createDto.getIdNumber() != null && !createDto.getIdNumber().isBlank()) {
            CustomerIdentifier id = new CustomerIdentifier();
            id.setCustomer(customer);
            id.setIdType(createDto.getIdType() != null ? createDto.getIdType() : ("INDIVIDUAL".equalsIgnoreCase(customer.getCustomerType()) ? "PASSPORT" : "INCORP_CERT"));
            id.setIdNumber(createDto.getIdNumber());
            id.setIssuingCountry(createDto.getIssuingCountry() != null ? createDto.getIssuingCountry().toUpperCase() : customer.getJurisdictionCountry());
            id.setPrimary(true);
            customer.getIdentifiers().add(id);
        }

        Customer saved = customerRepository.save(customer);

        auditService.recordEvent("KYC", "CREATE_CUSTOMER", "CUSTOMER", saved.getId().toString(),
                "Onboarded customer " + saved.getFullName() + " (" + saved.getCustomerNumber() + ") with risk: " + saved.getRiskRating());

        return mapToDto(saved);
    }

    public CustomerDto mapToDto(Customer customer) {
        CustomerDto dto = new CustomerDto();
        dto.setId(customer.getId());
        dto.setCustomerNumber(customer.getCustomerNumber());
        dto.setCustomerType(customer.getCustomerType());
        dto.setFullName(customer.getFullName());
        dto.setTradeName(customer.getTradeName());
        dto.setJurisdictionCountry(customer.getJurisdictionCountry());
        dto.setDateOfBirthOrIncorp(customer.getDateOfBirthOrIncorp());
        dto.setTaxIdentifier(customer.getTaxIdentifier());
        dto.setRiskRating(customer.getRiskRating());
        dto.setKycStatus(customer.getKycStatus());
        dto.setAccountStatus(customer.getAccountStatus());
        dto.setOnboardingDate(customer.getOnboardingDate());
        dto.setNextKycReviewDate(customer.getNextKycReviewDate());
        dto.setPep(customer.isPep());
        dto.setSanctionsHit(customer.isSanctionsHit());
        dto.setAdverseMediaHit(customer.isAdverseMediaHit());
        if (customer.getBranch() != null) {
            dto.setBranchCode(customer.getBranch().getCode());
        }
        return dto;
    }
}
