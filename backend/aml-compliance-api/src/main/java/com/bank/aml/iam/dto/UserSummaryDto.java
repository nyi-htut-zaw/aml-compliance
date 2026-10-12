package com.bank.aml.iam.dto;

import java.util.Set;
import java.util.UUID;

public class UserSummaryDto {
    private UUID id;
    private String username;
    private String email;
    private String fullName;
    private String status;
    private UUID branchId;
    private String branchCode;
    private String branchName;
    private Set<String> roles;
    private Set<String> permissions;

    public UserSummaryDto() {}

    public UserSummaryDto(UUID id, String username, String email, String fullName, String status,
                          UUID branchId, String branchCode, String branchName,
                          Set<String> roles, Set<String> permissions) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.status = status;
        this.branchId = branchId;
        this.branchCode = branchCode;
        this.branchName = branchName;
        this.roles = roles;
        this.permissions = permissions;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public UUID getBranchId() { return branchId; }
    public void setBranchId(UUID branchId) { this.branchId = branchId; }
    public String getBranchCode() { return branchCode; }
    public void setBranchCode(String branchCode) { this.branchCode = branchCode; }
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
}
