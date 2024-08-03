package com.inv.walletCare.logic.entity.incomeAllocation;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IncomeAllocationService {
    @Autowired
    private IncomeAllocationRepository incomeAllocationRepository;
    @Autowired
    private AccountRepository accountRepository;

    /**
     * This method transfers the incomes in case the account is eliminated or the users leave the shared account
     * @param ownerId is the owner of the incomes
     * @param accountId is the eliminated or leaved account id
     */
    public void transferIncomes(Long ownerId, Long accountId){
        var incomesToTransfer = incomeAllocationRepository.findAllByAccountIdAndOwner(ownerId, accountId);
        var userAccounts = accountRepository.findAllByOwnerId(ownerId);
        var mainAccount = userAccounts.get().stream().filter(Account::isDefault).findFirst().orElse(new Account());

        //Transfer Incomes
        for (var income : incomesToTransfer.get()) {
            income.map(updatedIncomeAllocation ->
            {
                updatedIncomeAllocation.setAccount(mainAccount);
                return incomeAllocationRepository.save(updatedIncomeAllocation);
            });
        }
    }
}
