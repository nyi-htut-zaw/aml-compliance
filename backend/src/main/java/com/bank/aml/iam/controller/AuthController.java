package com.bank.aml.iam.controller;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.dto.ApiResponse;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.iam.dto.AuthResponse;
import com.bank.aml.iam.dto.LoginRequest;
import com.bank.aml.iam.dto.UserSummaryDto;
import com.bank.aml.iam.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/auth", "/auth"})
@Tag(name = "Authentication & Access", description = "Login, token validation and current user context")
public class AuthController {

    private final AuthService authService;
    private final AuditService auditService;

    public AuthController(AuthService authService, AuditService auditService) {
        this.authService = authService;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT bearer token")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ApiResponse.ok("Authentication successful", response);
    }

    @GetMapping("/me")
    @Operation(summary = "Retrieve current authenticated user context, roles, and branch")
    public ApiResponse<UserSummaryDto> getCurrentUser() {
        UserSummaryDto user = authService.getCurrentUserProfile();
        return ApiResponse.ok(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "Log out and audit user session termination")
    public ApiResponse<Void> logout() {
        String username = SecurityUtils.getCurrentUsername();
        SecurityUtils.getCurrentUserId().ifPresent(userId ->
                auditService.recordEvent("IAM", "LOGOUT", "USER", userId.toString(), "User logged out")
        );
        return ApiResponse.ok("Logged out successfully", null);
    }
}
