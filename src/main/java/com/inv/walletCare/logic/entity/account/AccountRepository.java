package com.inv.walletCare.logic.entity.account;

import com.inv.walletCare.logic.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository for account entities.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by its name and owner's ID.
     *
     * @param name the name of the account
     * @param ownerId the ID of the account's owner
     * @return the account with the specified name and owner's ID
     */
    @Query("SELECT u FROM Account u WHERE u.name = ?1 AND u.owner.id = ?2 AND u.isDeleted = false")
    Optional<Account> findByNameAndOwnerId(String name, Long ownerId);
}