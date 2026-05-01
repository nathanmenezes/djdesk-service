package br.com.djdesk.service.domain.model;

import br.com.djdesk.service.shared.entity.AbstractEntity;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends AbstractEntity {

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false,
            foreignKey = @ForeignKey(name = "fk_refresh_tokens_user_id"))
    private User user;

    protected RefreshToken() {
        super();
    }

    public RefreshToken(String token, Instant expiresAt, User user) {
        super();
        this.token = token;
        this.expiresAt = expiresAt;
        this.revoked = false;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isRevoked() {
        return revoked;
    }

    public User getUser() {
        return user;
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public void revoke() {
        this.revoked = true;
    }
}
