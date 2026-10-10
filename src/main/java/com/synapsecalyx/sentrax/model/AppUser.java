package com.synapsecalyx.sentrax.model;

/** A dashboard login (analyst/admin) — distinct from a monitored Customer. */
public class AppUser {
    private long id;
    private String username;
    private String passwordHash;
    private String fullName;
    private Role role;

    public AppUser() {
    }

    public AppUser(long id, String username, String passwordHash, String fullName, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.role = role;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
