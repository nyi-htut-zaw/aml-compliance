package com.bank.aml.common.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

public final class SecurityUtils {

    private SecurityUtils() {}

    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
            return Optional.of(principal);
        }
        return Optional.empty();
    }

    public static String getCurrentUsername() {
        return getCurrentUserPrincipal().map(UserPrincipal::getUsername).orElse("SYSTEM");
    }

    public static Optional<UUID> getCurrentUserId() {
        return getCurrentUserPrincipal().map(UserPrincipal::getId);
    }

    public static Optional<UUID> getCurrentBranchId() {
        return getCurrentUserPrincipal().map(UserPrincipal::getBranchId);
    }

    public static boolean hasRole(String role) {
        return getCurrentUserPrincipal()
                .map(p -> p.getRoles().contains(role) || p.getRoles().contains("ROLE_" + role))
                .orElse(false);
    }

    public static boolean hasPermission(String permission) {
        return getCurrentUserPrincipal()
                .map(p -> p.getPermissions().contains(permission))
                .orElse(false);
    }
}
