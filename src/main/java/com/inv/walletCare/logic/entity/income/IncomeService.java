package com.inv.walletCare.logic.entity.income;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * This class is used to process the user's recurring income.
 */
@Service
public class IncomeService {

    @Autowired
    private IncomeRepository incomeRepository;

    /**
     * Process the user's income
     * @param frequency
     */
    @Async
    public void processIncome(FrequencyTypeEnum frequency)
    {
        System.out.println("Processing income");
    }
}