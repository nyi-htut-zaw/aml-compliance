package com.bank.aml.rules.repository;

import com.bank.aml.rules.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RuleRepository extends JpaRepository<Rule, UUID> {
    Optional<Rule> findByRuleCode(String ruleCode);
    List<Rule> findByActiveTrue();
    List<Rule> findByRuleTypeAndActiveTrue(String ruleType);
}
