package com.inv.walletCare.logic.entity.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository for account entities.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by its name and owner's ID.
     *
     * @param name    the name of the account
     * @param ownerId the ID of the account's owner
     * @return the account with the specified name and owner's ID
     */
    @Query("SELECT u FROM Account u WHERE u.name = ?1 AND u.owner.id = ?2 AND u.isDeleted = false")
    Optional<Account> findByNameAndOwnerId(String name, Long ownerId);

    /**
     * Finds all the accounts by its owner's ID.
     *
     * @param ownerId he ID of the account's owner
     * @return a lists of accounts with the specified owner's ID
     */
    @Query("SELECT u FROM Account u WHERE u.owner.id = ?1 AND u.isDeleted = false")
    Optional<List<Account>> findAllByOwnerId(Long ownerId);

    /**
     * Finds an account by its ID and owner's ID.
     *
     * @param id      the ID of the account
     * @param ownerId the ID of the account's owner
     * @return the account with the specified ID and owner's ID
     */
    @Query("SELECT u FROM Account u WHERE u.id = ?1 AND u.owner.id = ?2 AND u.isDeleted = false")
    Optional<Account> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT u FROM Account u WHERE u.owner.id = ?1 AND u.isDefault = true AND u.isDeleted = false")
    public Optional<Account> findDefaultAccountByOwnerId(Long ownerId);
}