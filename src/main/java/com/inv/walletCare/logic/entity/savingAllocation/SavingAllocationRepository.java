package com.inv.walletCare.logic.entity.savingAllocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SavingAllocationRepository extends JpaRepository<SavingAllocation, Long> {

    @Query("SELECT i FROM SavingAllocation i WHERE i.owner.id = ?1 AND i.account.id = ?2 AND i.isDeleted = false")
    Optional<List<Optional<SavingAllocation>>> findAllByAccountIdAndOwner(Long ownerId, Long accountId);

    @Query("SELECT i FROM SavingAllocation i WHERE i.saving.id = ?1 AND i.isDeleted = false")
    Optional<List<Optional<SavingAllocation>>> findAllBySavingId(Long savingId);
}
