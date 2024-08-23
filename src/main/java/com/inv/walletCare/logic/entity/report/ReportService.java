package com.inv.walletCare.logic.entity.report;

import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expenseCategory.ExpenseCategory;
import com.inv.walletCare.logic.entity.goal.Goal;
import com.inv.walletCare.logic.entity.goal.GoalRepository;
import com.inv.walletCare.logic.entity.goal.GoalStatusEnum;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.*;

/**
 * Service to use reports.
 */
@Service
public class ReportService {
    /**
     * Recurrence repository interface.
     */
    private final RecurrenceRepository recurrenceRepository;
    /**
     * Transaction repository interface.
     */
    private final TransactionRepository transactionRepository;
    /**
     * Goal repository interface.
     */
    private final GoalRepository goalRepository;

    /**
     * Service constructor in charge of initializing required repositories. Replace @autowire.
     * @param recurrenceRepository Recurrence repository interface.
     * @param transactionRepository Transaction repository interface.
     * @param goalRepository Goal repository interface.
     */
    public ReportService(RecurrenceRepository recurrenceRepository,
                         TransactionRepository transactionRepository,
                         GoalRepository goalRepository) {
        this.recurrenceRepository = recurrenceRepository;
        this.transactionRepository = transactionRepository;
        this.goalRepository = goalRepository;
    }

    /**
     * Convert a Date to LocalDate using toInstant().
     * @param dateToConvert Date to convert.
     * @return Equivalent of Date in LocalDate.
     */
    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Get a report with the sum of the expenses incurred in a given year by month and separate them by category.
     * @param year Int value to set year to search.
     * @param userId Long value with the user id.
     * @return List of BarchartDTO with the sum sorted by month and category.
     */
    public List<BarchartDTO> getYearlyExpenseByCategoryReport(int year, long userId) {
        List<Recurrence> recurrences = recurrenceRepository.findAllByOwner(userId).get();
        List<Transaction> transactions = transactionRepository.findAllExpensesByOwner(userId).get();
        List<Expense> expenses = new ArrayList<>();
        Map<String, Map<String, BigDecimal>> categoryMonthSums = new HashMap<>();

        // Get all recurrent expenses from user account
        for (var recurrence : recurrences.stream().filter(r -> r.getExpense() != null).toList()) {
            Expense recurringExpense = recurrence.getExpense();
            recurringExpense.setCreatedAt(recurrence.getCreatedAt());
            expenses.add(recurringExpense);
        }

        // Get all unique expenses from user account
        for (var transaction : transactions) {
            Expense expense = transaction.getExpense();
            expense.setCreatedAt(transaction.getCreatedAt());
            expenses.add(expense);
        }

        // Filter expenses by year
        for (Expense expense : expenses) {
            LocalDate expenseDate = convertToLocalDateViaInstant(expense.getCreatedAt());
            if (expenseDate.getYear() == year) {
                String month = expenseDate.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase();
                ExpenseCategory category = expense.getExpenseCategory();

                // Set "uncategorized" for missing or deleted categories
                if (category == null || category.getDeleted()) {
                    ExpenseCategory uncategorized = new ExpenseCategory();
                    uncategorized.setName("uncategorized");
                    category = uncategorized;
                }

                // Sum expenses by category
                categoryMonthSums.putIfAbsent(category.getName(), new HashMap<>());
                Map<String, BigDecimal> monthSums = categoryMonthSums.get(category.getName());

                monthSums.put(month, monthSums.getOrDefault(month, BigDecimal.ZERO)
                        .add(expense.getAmount()));
            }
        }

        List<BarchartDTO> yearlyReport = new ArrayList<>();

        for (Map.Entry<String, Map<String, BigDecimal>> categoryEntry : categoryMonthSums.entrySet()) {
            String category = categoryEntry.getKey();
            List<BarchartItemDTO> items = new ArrayList<>();

            for (Map.Entry<String, BigDecimal> monthEntry : categoryEntry.getValue().entrySet()) {
                BarchartItemDTO item = new BarchartItemDTO();
                item.setMonth(monthEntry.getKey());
                item.setAmount(monthEntry.getValue());
                items.add(item);
            }

            BarchartDTO barchartObject = new BarchartDTO();
            barchartObject.setCategory(category);
            barchartObject.setData(items);
            yearlyReport.add(barchartObject);
        }

        return yearlyReport;
    }

    /**
     * Get a report with the sum of the incomes incurred in a given year by month and separate them by category.
     * @param year Int value to set year to search.
     * @param userId Long value with the user id.
     * @return List of BarchartDTO with the sum sorted by month and category.
     */
    public List<BarchartDTO> getYearlyIncomeByCategoryReport(int year, long userId) {
        List<Recurrence> recurrences = recurrenceRepository.findAllByOwner(userId).get();
        List<Transaction> transactions = transactionRepository.findAllIncomesByOwner(userId).get();

        List<Income> incomes = new ArrayList<>();
        Map<String, Map<String, BigDecimal>> categoryMonthSums = new HashMap<>();

        // Get all recurrent incomes from user account
        for (var recurrence : recurrences.stream().filter(r -> r.getIncome() != null).toList()) {
            Income recurringIncome = recurrence.getIncome();
            recurringIncome.setCreatedAt(recurrence.getCreatedAt());
            incomes.add(recurringIncome);
        }

        // Get all unique incomes from user account
        for (var transaction : transactions) {
            Income income = transaction.getIncomeAllocation().getIncome();
            income.setCreatedAt(transaction.getCreatedAt());
            incomes.add(income);
        }

        // Filter incomes by year
        for (Income income : incomes) {
            LocalDate expenseDate = convertToLocalDateViaInstant(income.getCreatedAt());
            if (expenseDate.getYear() == year) {
                String month = expenseDate.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase();
                // Set "uncategorized" as default
                String category = "uncategorized";

                // Sum incomes by category
                categoryMonthSums.putIfAbsent(category, new HashMap<>());
                Map<String, BigDecimal> monthSums = categoryMonthSums.get(category);

                monthSums.put(month, monthSums.getOrDefault(month, BigDecimal.ZERO)
                        .add(income.getAmount()));
            }
        }

        List<BarchartDTO> yearlyReport = new ArrayList<>();

        for (Map.Entry<String, Map<String, BigDecimal>> categoryEntry : categoryMonthSums.entrySet()) {
            String category = categoryEntry.getKey();
            List<BarchartItemDTO> items = new ArrayList<>();

            for (Map.Entry<String, BigDecimal> monthEntry : categoryEntry.getValue().entrySet()) {
                BarchartItemDTO item = new BarchartItemDTO();
                item.setMonth(monthEntry.getKey());
                item.setAmount(monthEntry.getValue());
                items.add(item);
            }

            BarchartDTO barchartObject = new BarchartDTO();
            barchartObject.setCategory(category);
            barchartObject.setData(items);
            yearlyReport.add(barchartObject);
        }

        return yearlyReport;
    }

    /**
     * Get a report with the count of goals by status.
     * @param userId Long value with the user id.
     * @return List of BarchartDTO with the sum sorted by month and category.
     */
    public List<PiechartDTO> getGoalsProgressByStatus(long userId) {
        List<PiechartDTO> piecharts = new ArrayList<>();
        List<Goal> goals = goalRepository.findAllByOwnerId(userId);

        Map<GoalStatusEnum, Long> countByStatus = new HashMap<>();

        for (GoalStatusEnum status : GoalStatusEnum.values() ) {
            countByStatus.put(status, 0L);
        }

        // Count the goals based on their status
        for (Goal goal : goals) {
            GoalStatusEnum status = goal.getStatus();
            countByStatus.put(status, countByStatus.get(status) + 1);
        }

        // Convert the counts into PiechartDTO objects
        for (Map.Entry<GoalStatusEnum, Long> entry : countByStatus.entrySet()) {
            PiechartDTO piechart = new PiechartDTO();
            piechart.setCategory(entry.getKey().name());
            piechart.setData(entry.getValue());
            piecharts.add(piechart);
        }

        return piecharts;
    }
}
