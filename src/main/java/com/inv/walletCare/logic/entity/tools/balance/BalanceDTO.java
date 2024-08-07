package com.inv.walletCare.logic.entity.tools.balance;

import java.math.BigDecimal;

public class BalanceDTO {
    private BigDecimal monthlyExpenseBalance;
    private BigDecimal recurrentExpensesBalance;
    private  BigDecimal monthlyIncomeBalance;
    private  BigDecimal recurrentIncomesBalance;

    public BalanceDTO() {
    }

    public BalanceDTO(BigDecimal monthlyExpenseBalance, BigDecimal recurrentExpensesBalance, BigDecimal monthlyIncomeBalance, BigDecimal recurrentIncomesBalance) {
        this.monthlyExpenseBalance = monthlyExpenseBalance;
        this.recurrentExpensesBalance = recurrentExpensesBalance;
        this.monthlyIncomeBalance = monthlyIncomeBalance;
        this.recurrentIncomesBalance = recurrentIncomesBalance;
    }


    public BigDecimal getMonthlyExpenseBalance() {
        return monthlyExpenseBalance;
    }

    public void setMonthlyExpenseBalance(BigDecimal monthlyExpenseBalance) {
        this.monthlyExpenseBalance = monthlyExpenseBalance;
    }

    public BigDecimal getRecurrentExpensesBalance() {
        return recurrentExpensesBalance;
    }

    public void setRecurrentExpensesBalance(BigDecimal recurrentExpensesBalance) {
        this.recurrentExpensesBalance = recurrentExpensesBalance;
    }

    public BigDecimal getMonthlyIncomeBalance() {
        return monthlyIncomeBalance;
    }

    public void setMonthlyIncomeBalance(BigDecimal monthlyIncomeBalance) {
        this.monthlyIncomeBalance = monthlyIncomeBalance;
    }

    public BigDecimal getRecurrentIncomesBalance() {
        return recurrentIncomesBalance;
    }

    public void setRecurrentIncomesBalance(BigDecimal recurrentIncomesBalance) {
        this.recurrentIncomesBalance = recurrentIncomesBalance;
    }
}
