package com.inv.walletCare.logic.entity.income;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    @Query("SELECT i FROM Income i WHERE i.owner.id= ?1 AND i.isDeleted = false")
    List<Income> findAllByUserId(Long userId);

    @Query("SELECT i FROM Income i WHERE i.id = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Income> findByIdAndUserId(Long incomeId, Long userId);

    @Query("SELECT i FROM Income i WHERE i.name = ?1 AND i.owner.id = ?2 AND i.isDeleted = false AND i.isTemplate = true")
    Optional<Income> findByNameAndOwnerIdAAndTemplate(String name, Long ownerId);

}