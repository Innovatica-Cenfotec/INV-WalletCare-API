package com.inv.walletCare.logic.entity.tools.balance;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.helpers.Helper;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Handles the Balance calculations
 */
@Service
public class BalanceService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;


    /**
     * This method calculates all the balances in expenses and incomes for the account
     * @param account is the account that need the calculation
     * @return returns the balances for the account
     */
    public BalanceDTO balancesCalculationsbyAccount(Account account) {
        var transactions = transactionRepository.findAllByAccountId(account.getId());
        var reccurrentTransactions = recurrenceRepository.findAllByAccountId(account.getId());
        double monthlyExpense = 0;
        double monthlyIncome = 0;
        double recurrentExpense = 0;
        double recurrentIncome = 0;

        for (var tran : transactions.get()) {
            switch (tran.getType()) {
                case EXPENSE:
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        if (tran.getCreatedAt().getMonth() == new Date().getMonth()) {
                            monthlyExpense = monthlyExpense + tran.getAmount().doubleValue();
                        }
                    }
                    break;
                case INCOME:
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        if (tran.getCreatedAt().getMonth() == new Date().getMonth()) {
                            monthlyIncome = monthlyIncome + tran.getAmount().doubleValue();
                        }
                    }
                    break;
            }
        }

        for (var exp : reccurrentTransactions.get()) {
            if (exp.get().getExpense() != null) {
                recurrentExpense = recurrentExpense + exp.get().getExpense().getAmount().doubleValue();
            } else if (exp.get().getIncome() != null) {
                recurrentIncome = recurrentIncome + exp.get().getIncome().getAmount().doubleValue();
            }
        }


        return new BalanceDTO(new BigDecimal(monthlyExpense), Helper.reverse(new BigDecimal(recurrentExpense)), new BigDecimal(monthlyIncome), new BigDecimal(recurrentIncome));
    }


    /**
     * This method calculates all the balances in expenses and incomes for the user
     * @param user is the user that needs the calculations
     * @return returns de balances for the user
     */
    public BalanceDTO balancesCalculationsbyUser(User user) {
        var transactions = transactionRepository.findAllbyOwner(user.getId());
        var reccurrentTransactions = recurrenceRepository.findAllByOwner(user.getId());
        double monthlyExpense = 0;
        double monthlyIncome = 0;
        double recurrentExpense = 0;
        double recurrentIncome = 0;

        for (var tran : transactions.get()) {
            switch (tran.getType()) {
                case EXPENSE:
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        if (tran.getCreatedAt().getMonth() == new Date().getMonth()) {
                            monthlyExpense = monthlyExpense + tran.getAmount().doubleValue();
                        }
                    }
                    break;
                case INCOME:
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        if (tran.getCreatedAt().getMonth() == new Date().getMonth()) {
                            monthlyIncome = monthlyIncome + tran.getAmount().doubleValue();
                        }
                    }
                    break;
            }
        }

        for (var exp : reccurrentTransactions.get()) {
            if (exp.get().getExpense() != null) {
                recurrentExpense = recurrentExpense + exp.get().getExpense().getAmount().doubleValue();
            } else if (exp.get().getIncome() != null) {
                recurrentIncome = recurrentIncome + exp.get().getIncome().getAmount().doubleValue();
            }
        }

        return new BalanceDTO(new BigDecimal(monthlyExpense), Helper.reverse(new BigDecimal(recurrentExpense)), new BigDecimal(monthlyIncome), new BigDecimal(recurrentIncome));
    }

}
