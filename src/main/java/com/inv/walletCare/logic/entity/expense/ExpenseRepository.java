package com.inv.walletCare.logic.entity.expense;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    @Query("SELECT i FROM Expense i WHERE i.owner.id= ?1 AND i.isDeleted = false")
    List<Expense> findAllByUserId(Long userId);

    @Query("SELECT i FROM Expense i WHERE i.id = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Expense> findByIdAndUserId(Long incomeId, Long userId);

    @Query("SELECT i FROM Expense i WHERE i.name = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Expense> findByNameAndOwnerId(String name, Long ownerId);
}
