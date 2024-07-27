package com.inv.walletCare.logic.entity.tools;

import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ToolsService {

    @Autowired
    private AccountRepository accountRepository;
    public BigDecimal newBalanceAccount(Transaction transaction){
        var balance = accountRepository.findById(transaction.getAccount().getId()).get().getBalance();
        balance = balance.add(transaction.getAmount());
        return balance;
    }
}
