package com.bank.aml.iam.service;

import com.bank.aml.audit.service.AuditService;
import com.bank.aml.common.security.JwtTokenProvider;
import com.bank.aml.common.security.SecurityUtils;
import com.bank.aml.common.security.UserPrincipal;
import com.bank.aml.iam.dto.AuthResponse;
import com.bank.aml.iam.dto.LoginRequest;
import com.bank.aml.iam.dto.UserSummaryDto;
import com.bank.aml.iam.model.User;
import com.bank.aml.iam.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final int maxFailedLogins;
    private final int lockoutMinutes;
    private final long jwtExpirationMs;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtTokenProvider tokenProvider,
            UserRepository userRepository,
            AuditService auditService,
            @Value("${aml.security.max-failed-logins:5}") int maxFailedLogins,
            @Value("${aml.security.account-lockout-minutes:30}") int lockoutMinutes,
            @Value("${aml.security.jwt.expiration-ms:86400000}") long jwtExpirationMs) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.maxFailedLogins = maxFailedLogins;
        this.lockoutMinutes = lockoutMinutes;
        this.jwtExpirationMs = jwtExpirationMs;
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByUsername(request.getUsername());

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if ("LOCKED".equalsIgnoreCase(user.getStatus())) {
                if (user.getLockoutUntil() != null && Instant.now().isAfter(user.getLockoutUntil())) {
                    // Lockout period expired, reset status
                    user.setStatus("ACTIVE");
                    user.setFailedAttempts(0);
                    user.setLockoutUntil(null);
                    userRepository.save(user);
                } else {
                    throw new LockedException("User account is locked due to too many failed attempts. Try again later.");
                }
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            // Authentication succeeded, reset failed attempts
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                user.setFailedAttempts(0);
                user.setLockoutUntil(null);
                userRepository.save(user);
            }

            String token = tokenProvider.generateToken(authentication);
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            User user = userOpt.orElseThrow();
            String branchName = user.getBranch() != null ? user.getBranch().getName() : null;

            UserSummaryDto summary = new UserSummaryDto(
                    principal.getId(),
                    principal.getUsername(),
                    principal.getEmail(),
                    principal.getFullName(),
                    user.getStatus(),
                    principal.getBranchId(),
                    principal.getBranchCode(),
                    branchName,
                    principal.getRoles(),
                    principal.getPermissions()
            );

            auditService.recordEvent("IAM", "LOGIN", "USER", principal.getId().toString(), "Successful login");

            return new AuthResponse(token, jwtExpirationMs, summary);

        } catch (AuthenticationException ex) {
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                int attempts = user.getFailedAttempts() + 1;
                user.setFailedAttempts(attempts);
                if (attempts >= maxFailedLogins) {
                    user.setStatus("LOCKED");
                    user.setLockoutUntil(Instant.now().plus(lockoutMinutes, ChronoUnit.MINUTES));
                    auditService.recordEvent("IAM", "LOCKOUT", "USER", user.getId().toString(),
                            "Account automatically locked due to " + attempts + " failed attempts");
                }
                userRepository.save(user);
            }
            throw new BadCredentialsException("Invalid username or password");
        }
    }

    @Transactional(readOnly = true)
    public UserSummaryDto getCurrentUserProfile() {
        UserPrincipal principal = SecurityUtils.getCurrentUserPrincipal()
                .orElseThrow(() -> new BadCredentialsException("No authenticated principal found"));

        User user = userRepository.findById(principal.getId()).orElseThrow();
        String branchName = user.getBranch() != null ? user.getBranch().getName() : null;

        return new UserSummaryDto(
                principal.getId(),
                principal.getUsername(),
                principal.getEmail(),
                principal.getFullName(),
                user.getStatus(),
                principal.getBranchId(),
                principal.getBranchCode(),
                branchName,
                principal.getRoles(),
                principal.getPermissions()
        );
    }
}
