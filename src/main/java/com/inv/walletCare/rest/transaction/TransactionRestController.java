package com.inv.walletCare.rest.transaction;

import com.inv.walletCare.logic.entity.Response;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.helpers.Helper;
import com.inv.walletCare.logic.entity.tools.balance.BalanceDTO;
import com.inv.walletCare.logic.entity.tools.balance.BalanceService;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionRepository;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * This Rest Controoller handles the transactions
 */
@RequestMapping("/transactions")
@RestController
public class TransactionRestController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private BalanceService balanceService;

    /**
     * Retrieves all the transaction by the accountId
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public List<Transaction> getAllTrasactionsbyAccount(@PathVariable Long id){
        return transactionRepository.findAllByAccountId(id).get();
    }

    /**
     * Retrieves all the transactions by it´s owner
     * @return a list of transctions
     */
    @GetMapping("/owner")
    public List<Transaction> getAllTransactionsByOwner(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return transactionRepository.findAllbyOwner(currentUser.getId()).get();
    }

    /**
     * Retrieves the balances by account
     * @param id is the account ID
     * @return the calculations of balances by account
     */
    @GetMapping("/balances-account/{id}")
    public BalanceDTO getBalancesCalcByAccount(@PathVariable Long id){

        return balanceService.balancesCalculationsbyAccount(accountRepository.findById(id).get());
    }

    /**
     * Retrieves the balances by user
     * @return the calculation of balances by user
     */
    @GetMapping("/balances-user")
    public BalanceDTO getBalancesCalcByUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return balanceService.balancesCalculationsbyUser(currentUser);
    }


    /**
     * Retrieves all balances in the current year by month
     * @return A List of lists with the balances by month
     */
    @GetMapping("/balances-annually")
    public List<List<BigDecimal>> getBalancesAnnually(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return balanceService.annualBalancesByUser(currentUser);
    }

    /**
     * Retrieves all balances in the current month by Day
     * @return A List of lists with the balances by day
     */
    @GetMapping("/balances-monthly")
    public List<List<Double>> monthlyBalancesByUser() throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        return balanceService.monthlyBalancesByUser(currentUser);
    }

    /**
     * Make a rollback for a transaction
     * @param id is the transaction id
     * @return a message with the confirmation of the transaction
     * @throws Exception throws an Exception for various validations
     */
    @PostMapping("/rollback/{id}")
    public ResponseEntity<Response> rollbackTransaction(@PathVariable Long id) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        var existingTransaction = transactionRepository.findById(id);
        if(existingTransaction.isEmpty()){
            throw new Exception("La transacción no existe, inténtalo nuevamente con uan correcta. ");
        }

        //creates the new transaction for the rollback
        var newTransaction = existingTransaction.get().clone();
        newTransaction.setId(null);
        newTransaction.setDescription("Reversión de la transacción ID: " + existingTransaction.get().getId());
        newTransaction.setAmount(Helper.reverse(existingTransaction.get().getAmount()));
        newTransaction.setOwner(currentUser);
        newTransaction.rollbackTransactionType(existingTransaction.get().getType());
        transactionService.saveTransaction(newTransaction);

        //updates the existing transaction
        existingTransaction.map(rollbackTran -> {
            rollbackTran.setDeleted(true);
            rollbackTran.setDeletedAt(new Date());
            return transactionRepository.save(rollbackTran);
        });

        return ResponseEntity.ok(new Response("Ok"));
    }


}
