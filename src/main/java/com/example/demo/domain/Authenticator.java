package com.example.demo.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "authenticators")
public class Authenticator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_id", unique = true, nullable = false)
    private String credentialId;

    @Column(name = "public_key", nullable = false, length = 1024)
    private String publicKey;

    @Column(name = "signature_count")
    private long signatureCount = 0;

    @Column(name = "aaguid")
    private String aaguid;

    @Column(name = "transport")
    private String transport;

    @Column(name = "backup_eligible")
    private boolean backupEligible = false;

    @Column(name = "backup_state")
    private boolean backupState = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_used")
    private LocalDateTime lastUsed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Authenticator() {
        this.createdAt = LocalDateTime.now();
        this.lastUsed = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCredentialId() { return credentialId; }
    public void setCredentialId(String credentialId) { this.credentialId = credentialId; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public long getSignatureCount() { return signatureCount; }
    public void setSignatureCount(long signatureCount) { this.signatureCount = signatureCount; }

    public String getAaguid() { return aaguid; }
    public void setAaguid(String aaguid) { this.aaguid = aaguid; }

    public String getTransport() { return transport; }
    public void setTransport(String transport) { this.transport = transport; }

    public boolean isBackupEligible() { return backupEligible; }
    public void setBackupEligible(boolean backupEligible) { this.backupEligible = backupEligible; }

    public boolean isBackupState() { return backupState; }
    public void setBackupState(boolean backupState) { this.backupState = backupState; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUsed() { return lastUsed; }
    public void setLastUsed(LocalDateTime lastUsed) { this.lastUsed = lastUsed; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}