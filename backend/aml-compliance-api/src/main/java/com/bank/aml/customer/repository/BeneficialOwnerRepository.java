package com.bank.aml.customer.repository;

import com.bank.aml.customer.model.BeneficialOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BeneficialOwnerRepository extends JpaRepository<BeneficialOwner, UUID> {
    List<BeneficialOwner> findByEntityCustomerId(UUID entityCustomerId);
    List<BeneficialOwner> findByOwnerCustomerId(UUID ownerCustomerId);
}
