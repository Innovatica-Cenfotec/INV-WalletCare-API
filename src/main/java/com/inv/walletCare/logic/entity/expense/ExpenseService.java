package com.inv.walletCare.logic.entity.expense;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This class is used to process the user's recurring expenses.
 */
@Service
public class ExpenseService {


    /**
     * Process the user's expenses
     * @param frequency
     */
    @Async
    public void processExpense(FrequencyTypeEnum frequency) {
        System.out.println("expense");
    }
}