package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecurrenceRepository extends JpaRepository<Recurrence, Long> {

    @Query("SELECT i FROM Recurrence i WHERE  i.owner.id =?1 AND i.isDeleted = false")
    Optional<List<Recurrence>> findAllByOwner( Long owner);

    @Query("SELECT i FROM Recurrence i WHERE  i.account.id =?1 AND i.isDeleted = false")
    Optional<List<Optional<Recurrence>>> findAllByAccountId( Long accountId);

    @Query("SELECT i FROM Recurrence i WHERE i.isDeleted = false AND i.owner.id= ?1 AND i.account.id = ?2")
    List<Recurrence> findAllByOwnerAndAccountId(Long userId, Long accountID);

    @Query("SELECT i FROM Recurrence i WHERE i.account.isDeleted = false AND i.income.frequency = ?1")
    Optional<List<Recurrence>> findAllIncomeByFrequency(FrequencyTypeEnum frequency);

    @Query("SELECT i FROM Recurrence i WHERE i.account.isDeleted = false AND i.expense.frequency = ?1")
    Optional<List<Recurrence>> findAllExpenseByFrequency(FrequencyTypeEnum frequency);
}