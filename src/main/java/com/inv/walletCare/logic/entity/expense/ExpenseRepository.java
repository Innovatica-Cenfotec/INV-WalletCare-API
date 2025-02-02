package com.inv.walletCare.logic.entity.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    @Query("SELECT i FROM Expense i WHERE i.account.id = ?1 AND i.isDeleted = false")
    List<Expense> findAllByAccount(Long accountId);

    @Query("SELECT i FROM Expense i WHERE i.owner.id= ?1 AND i.isDeleted = false")
    List<Expense> findAllByUserId(Long userId);

    @Query("SELECT i FROM Expense i WHERE i.owner.id= ?1 AND i.isDeleted = false AND i.isTemplate = false")
    List<Expense> findAllNotTemplatesByUserId(Long userId);

    @Query("SELECT i FROM Expense i WHERE i.owner.id= ?1 AND i.isDeleted = false AND i.isTemplate = true")
    List<Expense> findAllTemplatesByUserId(Long userId);

    @Query("SELECT i FROM Expense i WHERE i.id = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Expense> findByIdAndUserId(Long incomeId, Long userId);

    @Query("SELECT i FROM Expense i WHERE i.name = ?1 AND i.owner.id = ?2 AND i.isDeleted = false AND i.isTemplate = true")
    Optional<Expense> findTemplateByNameAndOwnerId(String name, Long ownerId);

    @Query("SELECT i FROM Expense i WHERE i.owner.id = ?1 AND i.account.id =?2 AND i.isDeleted = false")
    Optional<List<Optional<Expense>>> findAllByAccountIdAndOwner(Long ownerId, Long accountId);

    @Query("SELECT i FROM Expense i WHERE  i.account.id =?1 AND i.isDeleted = false")
    Optional<List<Optional<Expense>>> findAllByAccountId( Long accountId);

    @Query("SELECT i FROM Expense i WHERE i.owner.id = ?1 AND i.isDeleted = false")
    List<Expense> findAllByOwnerId(Long ownerId);
}
