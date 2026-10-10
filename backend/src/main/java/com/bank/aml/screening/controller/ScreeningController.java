package com.bank.aml.screening.controller;

import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.dto.PagedResponse;
import com.bank.aml.screening.dto.ScreeningDispositionDto;
import com.bank.aml.screening.dto.ScreeningMatchDto;
import com.bank.aml.screening.dto.ScreeningRequestDto;
import com.bank.aml.screening.service.ScreeningEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({"/api/v1/screening", "/screening"})
@Tag(name = "Sanctions & PEP Screening", description = "Watchlist fuzzy matching, match scoring, and hit disposition")
public class ScreeningController {

    private final ScreeningEngineService screeningEngineService;
    private final com.bank.aml.screening.repository.WatchlistSourceRepository watchlistSourceRepository;

    public ScreeningController(ScreeningEngineService screeningEngineService,
                               com.bank.aml.screening.repository.WatchlistSourceRepository watchlistSourceRepository) {
        this.screeningEngineService = screeningEngineService;
        this.watchlistSourceRepository = watchlistSourceRepository;
    }

    @GetMapping("/watchlists")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST')")
    @Operation(summary = "Get active watchlist sources")
    public ApiResponse<List<com.bank.aml.screening.model.WatchlistSource>> getWatchlists() {
        return ApiResponse.ok(watchlistSourceRepository.findAll());
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST')")
    @Operation(summary = "Execute real-time fuzzy screening against Sanctions and PEP watchlists")
    public ApiResponse<List<ScreeningMatchDto>> screenCandidate(@Valid @RequestBody ScreeningRequestDto request) {
        List<ScreeningMatchDto> matches = screeningEngineService.performScreening(request);
        return ApiResponse.ok(matches);
    }

    @GetMapping("/matches/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST')")
    @Operation(summary = "Get list of pending screening match alerts for analyst triage")
    public ApiResponse<PagedResponse<ScreeningMatchDto>> getPendingMatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<ScreeningMatchDto> matches = screeningEngineService.getPendingScreeningMatches(
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "matchScore"))
        );
        return ApiResponse.ok(PagedResponse.of(matches));
    }

    @GetMapping("/matches/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST', 'AUDITOR')")
    @Operation(summary = "Get screening matches for a specific customer profile")
    public ApiResponse<List<ScreeningMatchDto>> getCustomerMatches(@PathVariable UUID customerId) {
        List<ScreeningMatchDto> matches = screeningEngineService.getMatchesForCustomer(customerId);
        return ApiResponse.ok(matches);
    }

    @PostMapping("/matches/{matchId}/disposition")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPLIANCE_OFFICER', 'MLRO', 'SENIOR_ANALYST', 'ANALYST')")
    @Operation(summary = "Adjudicate screening match (FALSE_POSITIVE, TRUE_POSITIVE, ESCALATED) with documented rationale")
    public ApiResponse<ScreeningMatchDto> adjudicateMatch(
            @PathVariable UUID matchId,
            @Valid @RequestBody ScreeningDispositionDto dispositionDto) {
        ScreeningMatchDto result = screeningEngineService.adjudicateMatch(matchId, dispositionDto);
        return ApiResponse.ok("Match adjudicated successfully", result);
    }
}
