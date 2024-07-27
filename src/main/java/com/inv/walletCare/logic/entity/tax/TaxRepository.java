package com.inv.walletCare.logic.entity.tax;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TaxRepository extends JpaRepository<Tax, Long> {

    @Query("SELECT i FROM Tax i WHERE i.owner.id= ?1 AND i.isDeleted = false")
    List<Tax> findAllByUserId(Long userId);

    @Query("SELECT i FROM Tax i WHERE i.id = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Tax> findByIdAndUserId(Long incomeId, Long userId);

    @Query("SELECT i FROM Tax i WHERE i.name = ?1 AND i.owner.id = ?2 AND i.isDeleted = false")
    Optional<Tax> findByNameAndOwnerId(String name, Long ownerId);
}