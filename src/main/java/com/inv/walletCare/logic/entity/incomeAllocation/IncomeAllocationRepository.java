package com.inv.walletCare.logic.entity.incomeAllocation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IncomeAllocationRepository extends JpaRepository<IncomeAllocation, Long> {
    @Query("SELECT i FROM IncomeAllocation i WHERE i.owner.id = ?1 AND i.account.id =?2 AND i.isDeleted = false")
    Optional<List<Optional<IncomeAllocation>>> findAllByAccountIdAndOwner(Long ownerId, Long accountId);
}
