package com.inv.walletCare.logic.entity.goal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    @Query("SELECT g FROM Goal g WHERE g.owner.id = ?1 AND g.isDeleted = false")
    List<Goal> findAllByOwnerId(Long userId);

    @Query("SELECT g FROM Goal g WHERE g.id = ?1 AND g.owner.id = ?2 AND g.isDeleted = false")
    Optional<Goal> findByIdAndOwnerId(Long id, Long userId);
}
