package com.inv.walletCare.rest.income;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/incomes")
public class IncomeRestController {

    @Autowired
    private IncomeRepository incomeRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TransactionService transactionService;

    @GetMapping
    public List<Income> getIncomes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return incomeRepository.findAllByUserId(currentUser.getId());
    }

    @PostMapping
    public Income addIncome(@Validated(OnCreate.class) @RequestBody Income income) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Validate that the income name is unique for the user
        Optional<Income> existingIncome = incomeRepository.findByNameAndOwnerId(income.getName(), currentUser.getId());
        if (existingIncome.isPresent()) {
            throw new FieldValidationException("name",
                    "El nombre del ingreso que ha ingresado ya está en uso. Por favor, ingresa uno diferente.");
        }

        // Validate that the account IDs are valid
        if (income.getIncomeAllocations() != null) {
            income.getIncomeAllocations().forEach(incomeAllocation -> {
                Optional<Account> account = accountRepository.findById(incomeAllocation.getAccount().getId());
                if (account.isEmpty()) {
                    throw new FieldValidationException("incomeAllocations",
                            "La cuenta con el ID " + incomeAllocation.getAccount().getId() + " no existe.");
                }

                // Validate that the account belongs to the current user
                if (account.get().getOwner().getId() != currentUser.getId()) {
                    throw new FieldValidationException("incomeAllocations",
                            "La cuenta con el ID " + incomeAllocation.getAccount().getId() +
                                    " no pertenece al usuario actual.");
                }
            });
        }

        if (income.isTaxRelated()) {
            // Validate that the tax IDs are valid
            Optional<Tax> tax = taxRepository.findById(income.getTax().getId());
            if (tax.isEmpty()) {
                throw new FieldValidationException("tax",
                        "El impuesto es requerido para los ingresos relacionados con impuestos.");
            }

            // Validate that the tax belongs to the current user
            if (tax.get().getOwner().getId() != currentUser.getId()) {
                throw new FieldValidationException("tax",
                        "El impuesto con el ID " + income.getTax().getId()
                                + " no existe o no pertenece al usuario actual.");
            }

            if (income.getFrequency() == null) {
                throw new FieldValidationException("frequency",
                        "La frecuencia es requerida para los ingresos relacionados con impuestos.");
            }

            if (income.getFrequency() == FrequencyTypeEnum.OTHER
                    && income.getScheduledDay() <= 1 || income.getScheduledDay() >= 31) {
                throw new FieldValidationException("scheduledDay",
                        "El día programado es requerido para los ingresos relacionados con impuestos.");
            }
        }

        Income newIncome = new Income();
        newIncome.setName(income.getName());
        newIncome.setAmount(income.getAmount());
        newIncome.setOwner(currentUser);
        newIncome.setTemplate(income.isTemplate());
        newIncome.setAmountType(income.getAmountType());
        newIncome.setFrequency(income.getFrequency());
        newIncome.setScheduledDay(income.getScheduledDay());
        newIncome.setTaxRelated(income.isTemplate());
        newIncome.setTax(income.getTax());
        newIncome.setCreatedAt(new Date());
        newIncome.setUpdatedAt(new Date());
        newIncome.setDeleted(false);
        newIncome.setType(income.getType());
        newIncome.setIncomeAllocations(income.getIncomeAllocations());

        var account = accountRepository.findById(Long.valueOf(1)).get();

        var tran = new Transaction();
        tran.setAmount(newIncome.getAmount());
        tran.setCreatedAt(new Date());
        tran.setDeletedAt(null);
        tran.setDescription("Ingreso: " + newIncome.getName());
        tran.setDeleted(false);
        tran.setPreviousBalance(new BigDecimal(0));
        tran.setType(TransactionTypeEnum.INCOME);
        tran.setUpdatedAt(null);
        tran.setAccount(account);
        tran.setExpenseAccount(null);
        tran.setIncomeAllocation(null);
        tran.setOwner(currentUser);
        tran.setSavingAllocation(null);
        transactionService.saveTransaction(tran);
        return incomeRepository.save(newIncome);
    }

    @GetMapping("/{id}")
    public Income getIncomeById(@PathVariable Long id) {
        Optional<Income> income = incomeRepository.findById(id);
        if (income.isEmpty()) {
            throw new IllegalArgumentException("Ingreso no encontrado o no pertenece al usuario actual.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the income belongs to the current user
        if (income.get().getOwner().getId() != currentUser.getId()) {
            throw new IllegalArgumentException("Ingreso no encontrado o no pertenece al usuario actual.");
        }

        return income.get();
    }
}
