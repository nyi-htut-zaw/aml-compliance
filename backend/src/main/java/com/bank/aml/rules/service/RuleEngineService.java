package com.bank.aml.rules.service;

import com.bank.aml.alert.model.Alert;
import com.bank.aml.alert.repository.AlertRepository;
import com.bank.aml.audit.service.AuditService;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.rules.model.Rule;
import com.bank.aml.rules.repository.RuleRepository;
import com.bank.aml.transaction.model.Account;
import com.bank.aml.transaction.model.Transaction;
import com.bank.aml.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class RuleEngineService {

    private static final Logger log = LoggerFactory.getLogger(RuleEngineService.class);

    private final RuleRepository ruleRepository;
    private final AlertRepository alertRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    private static final Set<String> HIGH_RISK_COUNTRIES = Set.of("PRK", "IRN", "MMR", "RUS", "SYR", "YEM", "CYP");

    public RuleEngineService(
            RuleRepository ruleRepository,
            AlertRepository alertRepository,
            TransactionRepository transactionRepository,
            AuditService auditService) {
        this.ruleRepository = ruleRepository;
        this.alertRepository = alertRepository;
        this.transactionRepository = transactionRepository;
        this.auditService = auditService;
    }

    @Transactional
    public List<Alert> evaluateTransaction(Transaction txn) {
        List<Rule> activeRules = ruleRepository.findByRuleTypeAndActiveTrue("TRANSACTION_MONITORING");
        List<Alert> generatedAlerts = new ArrayList<>();

        for (Rule rule : activeRules) {
            Alert alert = evaluateRule(rule, txn);
            if (alert != null) {
                generatedAlerts.add(alert);
            }
        }

        return generatedAlerts;
    }

    private Alert evaluateRule(Rule rule, Transaction txn) {
        Customer customer = txn.getCustomer();
        Account account = txn.getAccount();
        BigDecimal amount = txn.getBaseAmountUsd();

        switch (rule.getRuleCode()) {
            case "TM-STR-01": { // Structuring / Smurfing
                // Threshold between 8500 and 9999
                if (amount.compareTo(BigDecimal.valueOf(8500)) >= 0 && amount.compareTo(BigDecimal.valueOf(9999)) <= 0) {
                    Instant since = Instant.now().minus(72, ChronoUnit.HOURS);
                    List<Transaction> recent = transactionRepository.findRecentTransactionsByCustomer(customer.getId(), since);
                    long subThresholdCount = recent.stream()
                            .filter(t -> t.getBaseAmountUsd().compareTo(BigDecimal.valueOf(8500)) >= 0 &&
                                         t.getBaseAmountUsd().compareTo(BigDecimal.valueOf(9999)) <= 0)
                            .count();

                    if (subThresholdCount >= 1) { // Current + previous
                        return createAlert(
                                rule,
                                txn,
                                "Potential Structuring: Sub-threshold transfers under $10,000 detected",
                                "HIGH",
                                85.0,
                                24,
                                String.format("{\"ruleCode\":\"TM-STR-01\",\"count\":%d,\"amount\":%.2f,\"windowHours\":72}", subThresholdCount + 1, amount.doubleValue())
                        );
                    }
                }
                break;
            }
            case "TM-GEO-03": { // High-Risk Jurisdiction Corridor
                if (HIGH_RISK_COUNTRIES.contains(txn.getCounterpartyCountry().toUpperCase()) &&
                    amount.compareTo(BigDecimal.valueOf(10000)) >= 0) {
                    return createAlert(
                            rule,
                            txn,
                            "High-Risk Jurisdiction Transaction: " + txn.getCounterpartyCountry() + " corridor",
                            "CRITICAL",
                            95.0,
                            12,
                            String.format("{\"ruleCode\":\"TM-GEO-03\",\"country\":\"%s\",\"amount\":%.2f}", txn.getCounterpartyCountry(), amount.doubleValue())
                    );
                }
                break;
            }
            case "TM-LGT-05": { // Large Cash Movement
                if (("CASH_DEPOSIT".equalsIgnoreCase(txn.getTransactionType()) || "CASH_WITHDRAWAL".equalsIgnoreCase(txn.getTransactionType())) &&
                    amount.compareTo(BigDecimal.valueOf(10000)) >= 0) {
                    return createAlert(
                            rule,
                            txn,
                            "Large Cash Transaction Exceeding Mandatory Reporting Threshold: $" + amount,
                            "MEDIUM",
                            70.0,
                            48,
                            String.format("{\"ruleCode\":\"TM-LGT-05\",\"type\":\"%s\",\"amount\":%.2f}", txn.getTransactionType(), amount.doubleValue())
                    );
                }
                break;
            }
            case "TM-DOR-04": { // Sudden Inflow into Dormant Account
                if ("DORMANT".equalsIgnoreCase(account.getStatus()) && amount.compareTo(BigDecimal.valueOf(25000)) >= 0) {
                    return createAlert(
                            rule,
                            txn,
                            "Substantial Inflow into Dormant Account: " + account.getAccountNumber(),
                            "MEDIUM",
                            75.0,
                            48,
                            String.format("{\"ruleCode\":\"TM-DOR-04\",\"account\":\"%s\",\"amount\":%.2f}", account.getAccountNumber(), amount.doubleValue())
                    );
                }
                break;
            }
            case "TM-VEL-02": { // Rapid Movement / High Velocity
                if (amount.compareTo(BigDecimal.valueOf(50000)) >= 0) {
                    return createAlert(
                            rule,
                            txn,
                            "High Velocity Funds Movement Exceeding Single Transfer Velocity Threshold ($50,000)",
                            "HIGH",
                            88.0,
                            24,
                            String.format("{\"ruleCode\":\"TM-VEL-02\",\"amount\":%.2f}", amount.doubleValue())
                    );
                }
                break;
            }
            default:
                break;
        }

        return null;
    }

    private Alert createAlert(Rule rule, Transaction txn, String title, String severity, double score, int slaHours, String payload) {
        Alert alert = new Alert();
        alert.setAlertNumber("ALT-2026-" + ThreadLocalRandom.current().nextInt(1000, 9999));
        alert.setTitle(title);
        alert.setCustomer(txn.getCustomer());
        alert.setAccount(txn.getAccount());
        alert.setTransaction(txn);
        alert.setRule(rule);
        alert.setTriggerEvent("TRANSACTION");
        alert.setSeverity(severity);
        alert.setScore(BigDecimal.valueOf(score));
        alert.setStatus("NEW");
        alert.setSlaDueAt(Instant.now().plus(slaHours, ChronoUnit.HOURS));
        alert.setTriggerPayloadJson(payload);

        Alert saved = alertRepository.save(alert);
        log.warn("[ALERT-TRIGGERED] {} on Transaction {} - {}", saved.getAlertNumber(), txn.getReferenceNumber(), title);

        auditService.recordEvent("ALERTS", "GENERATE_ALERT", "ALERT", saved.getId().toString(),
                "Alert " + saved.getAlertNumber() + " triggered by rule " + rule.getRuleCode() + " on txn " + txn.getReferenceNumber());

        return saved;
    }
}
