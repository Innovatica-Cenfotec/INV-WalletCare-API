package com.inv.walletCare.logic.entity.saving;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavingRepository extends JpaRepository<Saving, Long> {

    @Query("SELECT s FROM Saving s WHERE s.owner.id = ?1 AND s.isDeleted = false")
    List<Saving> findByOwnerId(Long id);

    @Query("SELECT s FROM Saving s WHERE s.id = ?1 AND s.owner.id = ?2 AND s.isDeleted = false")
    Optional<Saving> findByIdAndUserId(Long savingId, Long userId);

    @Query("SELECT s FROM Saving s WHERE s.name = ?1 AND s.owner.id = ?2 AND s.isDeleted = false")
    Optional<Saving> findByNameAndOwnerId(String name, Long ownerId);
}
