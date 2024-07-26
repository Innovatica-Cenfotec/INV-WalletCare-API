package com.inv.walletCare.rest.transaction;

import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/transactions")
@RestController
public class TransactionRestController {

    @Autowired
    private TransactionRepository transactionRepository;
    @GetMapping("/{id}")
    public List<Transaction> getAllTrasactionsbyAccount(@PathVariable Long id){
        return transactionRepository.findAllByAccountId(id).get();
    }
}
