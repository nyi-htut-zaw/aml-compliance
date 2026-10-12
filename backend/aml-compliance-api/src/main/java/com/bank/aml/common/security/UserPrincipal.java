package com.bank.aml.common.security;

import com.bank.aml.iam.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String username;
    private final String email;
    private final String password;
    private final String fullName;
    private final UUID branchId;
    private final String branchCode;
    private final boolean active;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Set<String> roles;
    private final Set<String> permissions;

    public UserPrincipal(UUID id, String username, String email, String password, String fullName,
                         UUID branchId, String branchCode, boolean active,
                         Collection<? extends GrantedAuthority> authorities,
                         Set<String> roles, Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.fullName = fullName;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.active = active;
        this.authorities = authorities;
        this.roles = roles;
        this.permissions = permissions;
    }

    public static UserPrincipal create(User user) {
        Set<GrantedAuthority> authorities = new HashSet<>();
        Set<String> roles = new HashSet<>();
        Set<String> permissions = new HashSet<>();

        if (user.getRoles() != null) {
            user.getRoles().forEach(role -> {
                roles.add(role.getName());
                authorities.add(new SimpleGrantedAuthority(role.getName()));
                if (role.getPermissions() != null) {
                    role.getPermissions().forEach(permission -> {
                        permissions.add(permission.getCode());
                        authorities.add(new SimpleGrantedAuthority("PERM_" + permission.getCode()));
                    });
                }
            });
        }

        UUID branchId = user.getBranch() != null ? user.getBranch().getId() : null;
        String branchCode = user.getBranch() != null ? user.getBranch().getCode() : null;

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getFullName(),
                branchId,
                branchCode,
                "ACTIVE".equalsIgnoreCase(user.getStatus()),
                authorities,
                roles,
                permissions
        );
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getFullName() { return fullName; }
    public UUID getBranchId() { return branchId; }
    public String getBranchCode() { return branchCode; }
    public Set<String> getRoles() { return roles; }
    public Set<String> getPermissions() { return permissions; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return active; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return active; }
}
