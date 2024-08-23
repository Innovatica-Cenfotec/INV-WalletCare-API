package com.inv.walletCare.logic.entity.tools.balance;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.helpers.Helper;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
     *
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
     *
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
            if (exp.getExpense() != null) {
                recurrentExpense = recurrentExpense + exp.getExpense().getAmount().doubleValue();
            } else if (exp.getIncome() != null) {
                recurrentIncome = recurrentIncome + exp.getIncome().getAmount().doubleValue();
            }
        }

        return new BalanceDTO(new BigDecimal(monthlyExpense), Helper.reverse(new BigDecimal(recurrentExpense)), new BigDecimal(monthlyIncome), new BigDecimal(recurrentIncome));
    }

    /**
     * This method calculates all incomes and expenses annually
     *
     * @param user is the user that needs the calculations
     * @return a list of lists of incomes and expenses
     */
    public List<List<BigDecimal>> annualBalancesByUser(User user) {
        var transactions = transactionRepository.findAllbyOwner(user.getId());
        ArrayList<List<BigDecimal>> balances = new ArrayList<>();
        double[] expenses = new double[12];
        double[] incomes = new double[12];

        var annualExpenses = new ArrayList<BigDecimal>();
        var annualIncomes = new ArrayList<BigDecimal>();

        for (var tran : transactions.get()) {
            switch (tran.getType()) {
                case EXPENSE:
                    //Year validation
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        //Monthly Iteration
                        for (int i = 0; i <= 11; i++) {
                            //Month validations
                            if (tran.getCreatedAt().getMonth() == i) {
                                expenses[i] = expenses[i] + tran.getAmount().doubleValue();
                            }

                        }

                    }
                    break;
                case INCOME:
                    //Year Validation
                    if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                        //Monthly Iteration
                        for (int i = 0; i < 11; i++) {
                            //Month validation
                            if (tran.getCreatedAt().getMonth() == i) {
                                incomes[i] = incomes[i] + tran.getAmount().doubleValue();
                            }
                        }

                    }
                    break;
            }
        }

        for (var exp : expenses) {
            annualExpenses.add(Helper.reverse(new BigDecimal(exp)));
        }

        for (var in : incomes) {
            annualIncomes.add(new BigDecimal(in));
        }

        balances.add(annualExpenses);
        balances.add(annualIncomes);

        return balances;
    }

    public List<List<Double>> monthlyBalancesByUser(User user) throws Exception {
        var transactions = transactionRepository.findAllbyOwner(user.getId());
        ArrayList<List<Double>> info = new ArrayList<>();
        Double[] days = new Double[LocalDate.now().getDayOfMonth()];
        Double[] thisMonthExpenses = new Double[LocalDate.now().getDayOfMonth()];
        Double[] thisMonthIncomes = new Double[LocalDate.now().getDayOfMonth()];

        Arrays.fill(thisMonthExpenses, 0.0);
        Arrays.fill(thisMonthIncomes, 0.0);

        for (var tran : transactions.get()) {
            if (tran.getCreatedAt().getYear() == new Date().getYear()) {
                if (tran.getCreatedAt().getMonth() == new Date().getMonth()) {
                    for (int i = 1; i <= LocalDate.now().getDayOfMonth(); i++) {
                        days[i - 1] = Double.valueOf(i);
                        if (tran.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().getDayOfMonth() == i) {
                            if (tran.getType().equals(TransactionTypeEnum.EXPENSE)) {
                                thisMonthExpenses[i - 1] = thisMonthExpenses[i - 1] + Helper.reverse(tran.getAmount()).doubleValue();
                            } else if (tran.getType().equals(TransactionTypeEnum.INCOME)) {
                                thisMonthIncomes[i - 1] = thisMonthIncomes[i - 1] + tran.getAmount().doubleValue();
                            }
                        }
                    }
                }
            }

        }

        info.add(Arrays.asList(days));
        info.add(Arrays.asList(thisMonthExpenses));
        info.add(Arrays.asList(thisMonthIncomes));

        return info;
    }
}
