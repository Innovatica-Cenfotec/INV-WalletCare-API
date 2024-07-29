package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * This class is used to schedule the recurrences income and expenses of the user
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Autowired
    private IncomeRecurrenceService incomeService;

    @Autowired
    private ExpenseRecurrenceService expenseService;

    /**
     * Runs a task daily at midnight.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void runDaily() {
        incomeService.processIncome(FrequencyTypeEnum.DAILY);
        expenseService.processExpense(FrequencyTypeEnum.DAILY);
    }

    /**
     * Runs a task weekly at midnight on Mondays.
     */
    @Scheduled(cron = "0 0 0 * * MON")
    public void runWeekly() {
        incomeService.processIncome(FrequencyTypeEnum.WEEKLY);
        expenseService.processExpense(FrequencyTypeEnum.WEEKLY);
    }

    /**
     * Runs a task monthly at midnight on the first day of each month.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void runMonthly() {
        incomeService.processIncome(FrequencyTypeEnum.MONTHLY);
        expenseService.processExpense(FrequencyTypeEnum.MONTHLY);
    }

    /**
     * Runs a task annually at midnight on January 1st.
     */
    @Scheduled(cron = "0 0 0 1 1 ?")
    public void runAnnual() {
        incomeService.processIncome(FrequencyTypeEnum.ANNUAL);
        expenseService.processExpense(FrequencyTypeEnum.ANNUAL);
    }

    /**
     * Runs a task biweekly at midnight on the 1st and 15th of each month.
     */
    @Scheduled(cron = "0 0 0 1,15 * ?")
    public void runBiweekly() {
        incomeService.processIncome(FrequencyTypeEnum.BIWEEKLY);
        expenseService.processExpense(FrequencyTypeEnum.BIWEEKLY);
    }
}