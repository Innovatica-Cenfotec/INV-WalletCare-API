package com.inv.walletCare.logic.entity.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT u FROM Transaction u WHERE u.account.id = ?1 AND u.isDeleted = false")
    Optional<List<Transaction>> findAllByAccountId(Long accountId);

}