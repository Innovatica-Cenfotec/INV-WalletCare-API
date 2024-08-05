package com.inv.walletCare.logic.entity.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    /**
     * return all the transactions by account
     *
     * @param accountId is the account id
     * @return returns all transactions for the account
     */
    @Query("SELECT u FROM Transaction u WHERE u.account.id = ?1")
    Optional<List<Transaction>> findAllByAccountId(Long accountId);

    @Query("SELECT u FROM Transaction u WHERE u.owner.id = ?1")
    Optional<List<Transaction>> findAllbyOwner(Long ownerId);

    @Query("SELECT u FROM Transaction u WHERE u.owner.id = ?1 AND u.account.id = ?2")
    Optional<List<Optional<Transaction>>> findAllbyOwnerAndAccountId(Long ownerId, Long accountId);
}