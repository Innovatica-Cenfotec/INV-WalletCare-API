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

    public Transaction saveTransaction(Transaction newTransaction){
        return transactionRepository.save(newTransaction);
    }
}
