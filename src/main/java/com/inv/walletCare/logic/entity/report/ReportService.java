package com.inv.walletCare.logic.entity.report;

import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expense.ExpenseRepository;
import com.inv.walletCare.logic.entity.expenseCategory.ExpenseCategory;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
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
     * Expense repository interface.
     */
    private final ExpenseRepository expenseRepository;
    /**
     * Income repository interface.
     */
    private final IncomeRepository incomeRepository;

    /**
     * Service constructor in charge of initializing required repositories. Replace @autowire.
     * @param expenseRepository Expense repository interface.
     * @param incomeRepository Income repository interface.
     */
    public ReportService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
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
        List<Expense> expenses = expenseRepository.findAllNotTemplatesByUserId(userId);
        Map<String, Map<String, BigDecimal>> categoryMonthSums = new HashMap<>();

        for (Expense expense : expenses) {
            LocalDate expenseDate = convertToLocalDateViaInstant(expense.getCreatedAt());
            if (expenseDate.getYear() == year) {
                String month = expenseDate.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase();
                ExpenseCategory category = expense.getExpenseCategory();

                if (category == null) {
                    ExpenseCategory newCategory = new ExpenseCategory();
                    newCategory.setName("uncategorized");
                    category = newCategory;
                }

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
        List<Income> incomes = incomeRepository.findAllNotTemplatesByUserId(userId);
        Map<String, Map<String, BigDecimal>> categoryMonthSums = new HashMap<>();

        for (Income income : incomes) {
            LocalDate expenseDate = convertToLocalDateViaInstant(income.getCreatedAt());
            if (expenseDate.getYear() == year) {
                String month = expenseDate.getMonth()
                        .getDisplayName(TextStyle.SHORT, Locale.ENGLISH).toLowerCase();
                String category = "uncategorized";

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
}
