package com.inv.walletCare.logic.entity.report;

import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expense.ExpenseRepository;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ExpenseRepository expenseRepository;
    private final IncomeRepository incomeRepository;

    public ReportService(ExpenseRepository expenseRepository, IncomeRepository incomeRepository) {
        this.expenseRepository = expenseRepository;
        this.incomeRepository = incomeRepository;
    }

    public List<BarchartDTO> getYearlyExpenseByCategoryReport(int year, long userId) {
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);

        List<BarchartDTO> yearlyReport = new ArrayList<>();
        List<BarchartItemDTO> items = new ArrayList<>();

        BarchartItemDTO item = new BarchartItemDTO();
        item.setMonth("ene");
        item.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item);

        BarchartItemDTO item2 = new BarchartItemDTO();
        item2.setMonth("dic");
        item2.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item2);

        BarchartItemDTO item3 = new BarchartItemDTO();
        item3.setMonth("feb");
        item3.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item3);

        BarchartItemDTO item4 = new BarchartItemDTO();
        item4.setMonth("abr");
        item4.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item4);

        BarchartDTO barchartObject = new BarchartDTO();
        barchartObject.setCategory("comida");
        barchartObject.setData(items);

        yearlyReport.add(barchartObject);

        return yearlyReport;
    }

    public List<BarchartDTO> getYearlyIncomeByCategoryReport(int year, long userId) {
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);

        List<BarchartDTO> yearlyReport = new ArrayList<>();
        List<BarchartItemDTO> items = new ArrayList<>();

        BarchartItemDTO item = new BarchartItemDTO();
        item.setMonth("ene");
        item.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item);

        BarchartItemDTO item2 = new BarchartItemDTO();
        item2.setMonth("dic");
        item2.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item2);

        BarchartItemDTO item3 = new BarchartItemDTO();
        item3.setMonth("feb");
        item3.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item3);

        BarchartItemDTO item4 = new BarchartItemDTO();
        item4.setMonth("abr");
        item4.setAmount(BigDecimal.valueOf(120000.00));
        items.add(item4);

        BarchartDTO barchartObject = new BarchartDTO();
        barchartObject.setCategory("comida");
        barchartObject.setData(items);

        yearlyReport.add(barchartObject);

        return yearlyReport;
    }
}
