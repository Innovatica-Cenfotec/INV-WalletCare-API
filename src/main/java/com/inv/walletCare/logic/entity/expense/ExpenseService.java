package com.inv.walletCare.logic.entity.expense;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;

    public ExpenseService(ExpenseRepository expenseRepository, AccountRepository accountRepository) {
        this.expenseRepository = expenseRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * This method transfers the expenses in case the account is eliminated or the users leave the shared account
     * @param ownerId is the owner of the expenses
     * @param accountId is the eliminated or leaved account id
     */
    public void transferExpenses(Long ownerId, Long accountId){
        var expensesToTransfer = expenseRepository.findAllByAccountIdAndOwner(ownerId, accountId);
        var userAccounts = accountRepository.findAllByOwnerId(ownerId);
        var mainAccount = userAccounts.get().stream().filter(Account::isDefault).findFirst().orElse(new Account());

        //Tranfer Expense
        for (var expense : expensesToTransfer.get()) {
            expense.map(updatedExpense -> {
                updatedExpense.setAccount(mainAccount);
                return expenseRepository.save(updatedExpense);
            });
        }
    }
}
