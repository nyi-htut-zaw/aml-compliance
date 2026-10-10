package com.bank.aml.customer.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.customer.dto.BeneficialOwnerDto;
import com.bank.aml.customer.dto.UboGraphDto;
import com.bank.aml.customer.model.BeneficialOwner;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.customer.repository.BeneficialOwnerRepository;
import com.bank.aml.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BeneficialOwnershipService {

    private final BeneficialOwnerRepository beneficialOwnerRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;
    private final double uboThresholdPercentage;

    public BeneficialOwnershipService(
            BeneficialOwnerRepository beneficialOwnerRepository,
            CustomerRepository customerRepository,
            AuditService auditService,
            @Value("${aml.compliance.ubo-threshold-percentage:25.0}") double uboThresholdPercentage) {
        this.beneficialOwnerRepository = beneficialOwnerRepository;
        this.customerRepository = customerRepository;
        this.auditService = auditService;
        this.uboThresholdPercentage = uboThresholdPercentage;
    }

    @Transactional(readOnly = true)
    public List<BeneficialOwnerDto> getBeneficialOwnersForEntity(UUID entityCustomerId) {
        return beneficialOwnerRepository.findByEntityCustomerId(entityCustomerId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UboGraphDto calculateUboGraph(UUID entityCustomerId) {
        Customer entity = customerRepository.findById(entityCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", entityCustomerId));

        UboGraphDto graph = new UboGraphDto(entity.getId().toString(), entity.getFullName());

        // Root Node
        graph.getNodes().add(new UboGraphDto.UboNode(
                entity.getId().toString(),
                entity.getFullName(),
                "ROOT_ENTITY",
                BigDecimal.valueOf(100.0),
                false,
                entity.isSanctionsHit(),
                entity.isPep()
        ));

        List<BeneficialOwner> owners = beneficialOwnerRepository.findByEntityCustomerId(entityCustomerId);

        for (BeneficialOwner bo : owners) {
            String ownerId = bo.getOwnerCustomer() != null ? bo.getOwnerCustomer().getId().toString() : bo.getId().toString();
            boolean isSanctioned = bo.getOwnerCustomer() != null && bo.getOwnerCustomer().isSanctionsHit();
            boolean isPep = bo.getOwnerCustomer() != null && bo.getOwnerCustomer().isPep();
            boolean qualifiesAsUbo = bo.getOwnershipPercentage().doubleValue() >= uboThresholdPercentage;

            String nodeType = qualifiesAsUbo ? "INDIVIDUAL_UBO" : "MINORITY_SHAREHOLDER";

            graph.getNodes().add(new UboGraphDto.UboNode(
                    ownerId,
                    bo.getOwnerName(),
                    nodeType,
                    bo.getOwnershipPercentage(),
                    qualifiesAsUbo,
                    isSanctioned,
                    isPep
            ));

            graph.getEdges().add(new UboGraphDto.UboEdge(
                    ownerId,
                    entity.getId().toString(),
                    bo.getOwnershipPercentage(),
                    bo.getControlType()
            ));
        }

        return graph;
    }

    private BeneficialOwnerDto mapToDto(BeneficialOwner bo) {
        BeneficialOwnerDto dto = new BeneficialOwnerDto();
        dto.setId(bo.getId());
        dto.setEntityCustomerId(bo.getEntityCustomer().getId());
        if (bo.getOwnerCustomer() != null) {
            dto.setOwnerCustomerId(bo.getOwnerCustomer().getId());
        }
        dto.setOwnerName(bo.getOwnerName());
        dto.setNationality(bo.getNationality());
        dto.setOwnershipPercentage(bo.getOwnershipPercentage());
        dto.setControlType(bo.getControlType());
        dto.setUbo(bo.getOwnershipPercentage().doubleValue() >= uboThresholdPercentage);
        dto.setVerificationStatus(bo.getVerificationStatus());
        dto.setEffectiveFrom(bo.getEffectiveFrom());
        return dto;
    }
}
