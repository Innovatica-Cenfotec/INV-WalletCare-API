package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocation;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * This class is used to process the user's recurring expenses.
 */
@Service
public class ExpenseRecurrenceService {

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * Process the user's expenses
     *
     * @param frequency
     */
    @Async
    public void processExpense(FrequencyTypeEnum frequency) {
        Optional<List<Recurrence>> recurrences = recurrenceRepository.findAllExpenseByFrequency(frequency);
        if (recurrences.isEmpty()) {
            return;
        }

        recurrences.get().forEach(recurrence -> {
            var expense = recurrence.getExpense();

            var tran = new Transaction();
            tran.setAmount(expense.getAmount());
            tran.setOwner(expense.getOwner());
            tran.setDescription("Gasto recurrente: " + expense.getName());
            tran.setPreviousBalance(new BigDecimal(0));
            tran.setType(TransactionTypeEnum.EXPENSE);
            tran.setExpense(expense);
            tran.setIncomeAllocation(null);
            tran.setSavingAllocation(null);
            tran.setCreatedAt(new Date());
            tran.setUpdatedAt(new Date());
            tran.setDeleted(false);

            try {
                transactionService.saveTransaction(tran);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}