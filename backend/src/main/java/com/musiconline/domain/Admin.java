package com.musiconline.domain;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Separate admin account table (mo_admins).
 * PDF Task 2 requires dedicated tables for admin, registered users, and music.
 * Admins are stored here AND also in mo_users (with role=ADMIN) so Spring Security
 * can authenticate them through the unified user details service.
 */
@Entity
@Table(name = "mo_admins")
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Links to the corresponding mo_users record */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    /** Admin-specific: level of privilege (e.g. "SUPER", "MODERATOR") */
    @Column(nullable = false, length = 30)
    private String adminLevel = "MODERATOR";

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant lastLoginAt = Instant.now();

    // ── Getters and setters ──────────────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getAdminLevel() { return adminLevel; }
    public void setAdminLevel(String adminLevel) { this.adminLevel = adminLevel; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(Instant lastLoginAt) { this.lastLoginAt = lastLoginAt; }
}
