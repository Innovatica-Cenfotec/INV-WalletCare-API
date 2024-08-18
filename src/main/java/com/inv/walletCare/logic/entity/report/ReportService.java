package com.inv.walletCare.logic.entity.report;

import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expense.ExpenseRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReportService {
    private final ExpenseRepository expenseRepository;

    public ReportService(ExpenseRepository expenseRepository) {
        this.expenseRepository = expenseRepository;
    }

    public List<ExpenseReportDTO> getYearlyExpenseReport(long userId) {
        List<Expense> expenses = expenseRepository.findAllByUserId(userId);
        return null;
    }
}
