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

import static org.apache.commons.lang3.BooleanUtils.forEach;

/**
 * This class is used to process the user's recurring income.
 */
@Service
public class IncomeRecurrenceService {

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * Process the user's income
     * @param frequency
     */
    @Async
    public void processIncome(FrequencyTypeEnum frequency)
    {
        Optional<List<Recurrence>> recurrences = recurrenceRepository.findAllIncomeByFrequency(frequency);
        if (recurrences.isEmpty()) {
            return;
        }

        recurrences.get().forEach(recurrence -> {
            var income = recurrence.getIncome();

            // Create a transaction for each income
            //income.getIncomeAllocations().forEach(incomeAllocation -> {

             //   var tran = new Transaction();
            //    tran.setAmount(incomeAllocation.getPercentage().multiply(income.getAmount()));
             //   tran.setOwner(income.getOwner());
             //   tran.setDescription("Ingreso recurrente: " + income.getName());
              //  tran.setPreviousBalance(new BigDecimal(0));
             //   tran.setType(TransactionTypeEnum.EXPENSE);
              //  tran.setExpense(null);
              //  tran.setIncomeAllocation(incomeAllocation);
               // tran.setSavingAllocation(null);
               // tran.setCreatedAt(new Date());
               // tran.setUpdatedAt(new Date());
               // tran.setDeleted(false);

              //  try {
              //      transactionService.saveTransaction(tran);
             //   } catch (Exception e) {
              //      e.printStackTrace();
             //   }
           // });
        });
    }
}