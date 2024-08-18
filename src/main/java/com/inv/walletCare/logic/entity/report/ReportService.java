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

@Service
public class ReportService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public ReportService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

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
