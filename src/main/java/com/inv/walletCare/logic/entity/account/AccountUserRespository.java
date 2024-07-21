package com.inv.walletCare.logic.entity.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountUserRespository extends JpaRepository<AccountUser, Long> {

    /**
     * Finds an {@link AccountUser} by the user ID where the account is not marked as deleted.
     *
     * @param userID The ID of the user to find the account user relationship for.
     * @return An {@link Optional} containing the found {@link AccountUser} if present, or an empty {@link Optional} if not found.
     */
    @Query("SELECT u FROM AccountUser u WHERE  u.user.id = ?1 AND u.isDeleted = false AND u.account.isDeleted = false")
    Optional<AccountUser> findAllByUserId(Long userID);

    @Query("SELECT u FROM AccountUser u WHERE u.account.id = ?1 AND u.user.id = ?2 AND u.isDeleted = false AND u.account.isDeleted = false")
    Optional<AccountUser> findByUserIdAndAccountId(Long userId, Long accountId);

    @Query("SELECT u FROM AccountUser u WHERE u.account.id = ?1 AND u.isDeleted = false AND u.account.isDeleted = false")
    Optional<List<AccountUser>> findAllByAccountID(Long accountID);
}