package com.bank.aml.screening.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.exception.ResourceNotFoundException;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.common.util.FuzzyMatchUtils;
import com.bank.aml.customer.model.Customer;
import com.bank.aml.customer.repository.CustomerRepository;
import com.bank.aml.iam.model.User;
import com.bank.aml.iam.repository.UserRepository;
import com.bank.aml.screening.dto.ScreeningDispositionDto;
import com.bank.aml.screening.dto.ScreeningMatchDto;
import com.bank.aml.screening.dto.ScreeningRequestDto;
import com.bank.aml.screening.model.ScreeningMatch;
import com.bank.aml.screening.model.ScreeningRun;
import com.bank.aml.screening.model.WatchlistEntry;
import com.bank.aml.screening.repository.ScreeningMatchRepository;
import com.bank.aml.screening.repository.ScreeningRunRepository;
import com.bank.aml.screening.repository.WatchlistEntryRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;

@Service
public class ScreeningEngineService {

    private final WatchlistEntryRepository watchlistEntryRepository;
    private final ScreeningRunRepository screeningRunRepository;
    private final ScreeningMatchRepository screeningMatchRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;

    public ScreeningEngineService(
            WatchlistEntryRepository watchlistEntryRepository,
            ScreeningRunRepository screeningRunRepository,
            ScreeningMatchRepository screeningMatchRepository,
            CustomerRepository customerRepository,
            UserRepository userRepository,
            AuditService auditService) {
        this.watchlistEntryRepository = watchlistEntryRepository;
        this.screeningRunRepository = screeningRunRepository;
        this.screeningMatchRepository = screeningMatchRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
    }

    @Transactional
    public List<ScreeningMatchDto> performScreening(ScreeningRequestDto request) {
        List<WatchlistEntry> entries = watchlistEntryRepository.findBySourceActiveTrue();
        double threshold = request.getThresholdScore() != null ? request.getThresholdScore() : 80.0;

        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId()).orElse(null);
        }

        UUID runByUserId = SecurityUtils.getCurrentUserId().orElse(null);
        User runByUser = runByUserId != null ? userRepository.findById(runByUserId).orElse(null) : null;

        ScreeningRun run = new ScreeningRun();
        run.setEntityType(customer != null ? "CUSTOMER" : "AD_HOC_QUERY");
        run.setEntityId(customer != null ? customer.getId().toString() : request.getName());
        run.setRunType("ON_DEMAND");
        run.setTotalCandidates(entries.size());
        run.setRunByUser(runByUser);
        run = screeningRunRepository.save(run);

        List<ScreeningMatchDto> results = new ArrayList<>();
        int matchesCount = 0;

        for (WatchlistEntry entry : entries) {
            double bestScore = FuzzyMatchUtils.calculateBestMatchScore(request.getName(), entry.getPrimaryName());
            String matchedTerm = entry.getPrimaryName();

            if (entry.getAliases() != null && !entry.getAliases().isBlank()) {
                for (String alias : entry.getAliases().split(",")) {
                    double aliasScore = FuzzyMatchUtils.calculateBestMatchScore(request.getName(), alias.trim());
                    if (aliasScore > bestScore) {
                        bestScore = aliasScore;
                        matchedTerm = alias.trim();
                    }
                }
            }

            // Country comparison bonus (+5%)
            if (request.getCountryCode() != null && entry.getCountryCodes() != null &&
                entry.getCountryCodes().toUpperCase().contains(request.getCountryCode().toUpperCase())) {
                bestScore = Math.min(100.0, bestScore + 5.0);
            }

            // Date of birth comparison bonus (+5%)
            if (request.getDateOfBirth() != null && entry.getDateOfBirth() != null &&
                entry.getDateOfBirth().contains(request.getDateOfBirth())) {
                bestScore = Math.min(100.0, bestScore + 5.0);
            }

            if (bestScore >= threshold) {
                matchesCount++;
                ScreeningMatch match = new ScreeningMatch();
                match.setScreeningRun(run);
                match.setCustomer(customer != null ? customer : createPlaceholderCustomer(request));
                match.setWatchlistEntry(entry);
                match.setMatchType(entry.getSource().getListType());
                match.setMatchScore(BigDecimal.valueOf(bestScore).setScale(2, RoundingMode.HALF_UP));
                match.setMatchingAlgorithm("JARO_WINKLER_TOKEN_SET");
                match.setMatchedTerm(matchedTerm);
                match.setDisposition("PENDING");
                match.setMatchDetailsJson(String.format("{\"query\":\"%s\",\"matchedTerm\":\"%s\",\"score\":%.2f}",
                        request.getName(), matchedTerm, bestScore));

                ScreeningMatch saved = screeningMatchRepository.save(match);
                results.add(mapToDto(saved));
            }
        }

        run.setMatchesFound(matchesCount);
        run.setCompletedAt(Instant.now());
        screeningRunRepository.save(run);

        auditService.recordEvent("SCREENING", "SCREENING_RUN", "SCREENING_RUN", run.getId().toString(),
                "Screened query '" + request.getName() + "' across " + entries.size() + " records. Hits found: " + matchesCount);

        return results;
    }

    private Customer createPlaceholderCustomer(ScreeningRequestDto request) {
        // Return existing first customer or fallback for ad-hoc queries without customer id
        return customerRepository.findAll().stream().findFirst().orElseThrow();
    }

    @Transactional
    public ScreeningMatchDto adjudicateMatch(UUID matchId, ScreeningDispositionDto dispositionDto) {
        ScreeningMatch match = screeningMatchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("ScreeningMatch", "id", matchId));

        UUID userId = SecurityUtils.getCurrentUserId().orElse(null);
        User user = userId != null ? userRepository.findById(userId).orElse(null) : null;

        match.setDisposition(dispositionDto.getDisposition());
        match.setDispositionNotes(dispositionDto.getNotes());
        match.setDispositionByUser(user);
        match.setDispositionAt(Instant.now());

        Customer customer = match.getCustomer();
        if ("TRUE_POSITIVE".equalsIgnoreCase(dispositionDto.getDisposition())) {
            if ("SANCTIONS".equalsIgnoreCase(match.getMatchType())) {
                customer.setSanctionsHit(true);
                customer.setRiskRating("CRITICAL");
                customer.setAccountStatus("BLOCKED");
            } else if ("PEP".equalsIgnoreCase(match.getMatchType())) {
                customer.setPep(true);
                customer.setRiskRating("HIGH");
                customer.setKycStatus("UNDER_EDD");
            }
            customerRepository.save(customer);
        }

        ScreeningMatch saved = screeningMatchRepository.save(match);

        auditService.recordEvent("SCREENING", "DISPOSITION", "SCREENING_MATCH", saved.getId().toString(),
                "Adjudicated match on " + match.getMatchedTerm() + " to " + dispositionDto.getDisposition());

        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ScreeningMatchDto> getPendingScreeningMatches(Pageable pageable) {
        return screeningMatchRepository.findByDisposition("PENDING", pageable).map(this::mapToDto);
    }

    @Transactional(readOnly = true)
    public List<ScreeningMatchDto> getMatchesForCustomer(UUID customerId) {
        return screeningMatchRepository.findByCustomerIdOrderByMatchScoreDesc(customerId).stream()
                .map(this::mapToDto)
                .toList();
    }

    public ScreeningMatchDto mapToDto(ScreeningMatch match) {
        ScreeningMatchDto dto = new ScreeningMatchDto();
        dto.setId(match.getId());
        dto.setCustomerId(match.getCustomer().getId());
        dto.setCustomerName(match.getCustomer().getFullName());
        dto.setWatchlistEntryId(match.getWatchlistEntry().getId());
        dto.setPrimaryName(match.getWatchlistEntry().getPrimaryName());
        dto.setAliases(match.getWatchlistEntry().getAliases());
        dto.setSourceName(match.getWatchlistEntry().getSource().getName());
        dto.setSourceCode(match.getWatchlistEntry().getSource().getCode());
        dto.setMatchType(match.getMatchType());
        dto.setMatchScore(match.getMatchScore());
        dto.setMatchingAlgorithm(match.getMatchingAlgorithm());
        dto.setMatchedTerm(match.getMatchedTerm());
        dto.setPepCategory(match.getWatchlistEntry().getPepCategory());
        dto.setDesignatorPrograms(match.getWatchlistEntry().getDesignatorPrograms());
        dto.setDisposition(match.getDisposition());
        dto.setDispositionNotes(match.getDispositionNotes());
        if (match.getDispositionByUser() != null) {
            dto.setDispositionByUsername(match.getDispositionByUser().getUsername());
        }
        dto.setDispositionAt(match.getDispositionAt());
        dto.setCreatedAt(match.getCreatedAt());
        return dto;
    }
}
