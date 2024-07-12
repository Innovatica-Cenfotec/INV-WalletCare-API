package com.inv.walletCare.logic.entity.auth.invalidate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class InvalidateToken {
    @Id
    private String token;
    private LocalDateTime invalidateAt;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getInvalidateAt() {
        return invalidateAt;
    }

    public void setInvalidateAt(LocalDateTime invalidateAt) {
        this.invalidateAt = invalidateAt;
    }
}
