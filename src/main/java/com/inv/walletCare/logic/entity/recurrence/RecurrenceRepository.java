package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.expenseRecurrence.ExpenseRecurrence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RecurrenceRepository extends JpaRepository<Recurrence, Long> {

    @Query("""
            SELECT i FROM Recurrence i WHERE
            i.account.isDeleted = false AND
            i.income.frequency = ?1
           """)
    Optional<List<Recurrence>> findAllIncomeByFrequency(FrequencyTypeEnum frequency);


    @Query("""
            SELECT i FROM Recurrence i WHERE
            i.account.isDeleted = false AND
            i.expense.frequency = ?1
           """)
    Optional<List<Recurrence>> findAllExpenseByFrequency(FrequencyTypeEnum frequency);

    @Query("SELECT i FROM Recurrence i WHERE i.isDeleted = false AND i.owner.id= ?1")
    List<Recurrence> findAllByOwner(Long userId);
}