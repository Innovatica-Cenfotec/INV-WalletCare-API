package com.inv.walletCare.logic.entity.auth.invalidate;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidateTokenRepository extends JpaRepository<InvalidateToken, String> {
}
