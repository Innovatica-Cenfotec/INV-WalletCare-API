package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.savingAllocation.SavingAllocationRepository;
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
 * Service class for processing recurring savings.
 */
@Service
public class SavingRecurrenceService {

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private SavingAllocationRepository savingAllocationRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * Processes savings based on the given frequency.
     * This method is asynchronous and runs in a separate thread.
     *
     * @param frequency the frequency type of the recurring savings
     */
    @Async
    public void processSaving(FrequencyTypeEnum frequency) {
        // Retrieve all recurrences for the given frequency
        Optional<List<Recurrence>> recurrences = recurrenceRepository.findAllSavingByFrequency(frequency);
        if (recurrences.isEmpty()) {
            return;
        }

        // Process each recurrence
        recurrences.get().forEach(recurrence -> {
            var saving = recurrence.getSaving();

            // Retrieve all saving allocations for the given saving
            savingAllocationRepository.findAllBySavingId(saving.getId()).get().forEach(savingAllocation -> {

                // Create a new transaction for the saving
                var transaction = new Transaction();
                transaction.setAmount(savingAllocation.get().getAmount().multiply(saving.getAmount()));
                transaction.setOwner(saving.getOwner());
                transaction.setDescription("Ahorro recurrente: " + saving.getName());
                transaction.setPreviousBalance(new BigDecimal(0));
                transaction.setType(TransactionTypeEnum.SAVING);
                transaction.setExpense(null);
                transaction.setIncomeAllocation(null);
                transaction.setSavingAllocation(savingAllocation.get());
                transaction.setCreatedAt(new Date());
                transaction.setUpdatedAt(new Date());
                transaction.setDeleted(false);

                try {
                    // Save the transaction
                    transactionService.saveTransaction(transaction);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }
}