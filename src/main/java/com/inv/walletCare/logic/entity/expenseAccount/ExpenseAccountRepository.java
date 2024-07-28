package com.inv.walletCare.logic.entity.expenseAccount;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExpenseAccountRepository extends JpaRepository<ExpenseAccount, Long> {

    @Query("""
            SELECT i FROM ExpenseAccount i WHERE
            i.owner.id= ?1 AND
            i.account.type = 'RECURRENCE' AND
            i.account.isDeleted = false AND
            i.expense.frequency = ?2
           """)
    Optional<List<ExpenseAccount>> findAllByFrequency(Long userId, FrequencyTypeEnum frequency);
}