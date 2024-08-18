package com.inv.walletCare.logic.entity.expenseCategory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExpenseCategoryRepository extends JpaRepository<ExpenseCategory,Long> {

    @Query("SELECT u FROM ExpenseCategory u WHERE u.id = ?1 AND u.owner.id = ?2 AND u.isDeleted = false")
    Optional<ExpenseCategory> findByIdAndOwnerId(Long id, Long ownerId);

    @Query("SELECT u FROM ExpenseCategory u WHERE u.name = ?1 AND u.owner.id = ?2 AND u.isDeleted = false")
    Optional<ExpenseCategory> findByNameAndOwnerId(String name, Long ownerId);

    @Query("SELECT u FROM ExpenseCategory u WHERE u.owner.id = ?1 AND u.isDeleted = false")
    List<ExpenseCategory> findAllByOwnerId(Long ownerId);
}