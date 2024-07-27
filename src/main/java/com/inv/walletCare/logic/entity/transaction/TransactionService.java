package com.inv.walletCare.logic.entity.transaction;

import com.inv.walletCare.logic.entity.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Transaction saveTransaction(Transaction newTransaction) throws Exception {
        var account = accountRepository.findById(newTransaction.getAccount().getId());
        if (account.isEmpty()) {
            throw new Exception("La cuenta indicada no existe, favor intenta con una existente.");
        }
        //calcs the new balance for the account
        var actualbalance = account.get().getBalance();
        var newBalance = actualbalance.add(newTransaction.getAmount());

        //updates the balance in the account
        account.map(existingAccount -> {
            existingAccount.setBalance(newBalance);
            return accountRepository.save(existingAccount);
        });
        return transactionRepository.save(newTransaction);
    }
}
