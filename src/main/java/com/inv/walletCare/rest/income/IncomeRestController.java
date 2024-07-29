package com.inv.walletCare.rest.income;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocation;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocationRepository;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
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

    @Autowired
    private IncomeAllocationRepository incomeAllocationRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

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
        newIncome.setTaxRelated(income.isTaxRelated());
        newIncome.setTax(income.getTax());
        newIncome.setCreatedAt(new Date());
        newIncome.setUpdatedAt(new Date());
        newIncome.setDeleted(false);
        newIncome.setType(income.getType());
        var incomeCreated = incomeRepository.save(newIncome);

        if (income.isAddTransaction()){
            Optional<Account> account = accountRepository.findById(income.getAccount().getId());
            if (account.isEmpty()) {
                throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
            }

            if (incomeCreated.getType().equals(IncomeExpenceType.UNIQUE)) {
                // Associate the income with the account
                IncomeAllocation incomeAllocation = new IncomeAllocation();
                incomeAllocation.setAccount(account.get());
                incomeAllocation.setIncome(newIncome);
                incomeAllocation.setOwner(currentUser);
                incomeAllocation.setPercentage(new BigDecimal(100));
                incomeAllocation.setCreatedAt(new Date());
                incomeAllocation.setUpdatedAt(new Date());
                incomeAllocation.setDeleted(false);
                var incomeAllocationCreated = incomeAllocationRepository.save(incomeAllocation);

                // Create a transaction for the income
                var tran = new Transaction();
                tran.setAmount(newIncome.getAmount());
                tran.setAccount(account.get());
                tran.setCreatedAt(new Date());
                tran.setDeletedAt(null);
                tran.setDescription("Ingreso: " + newIncome.getName());
                tran.setDeleted(false);
                tran.setPreviousBalance(new BigDecimal(0));
                tran.setType(TransactionTypeEnum.INCOME);
                tran.setUpdatedAt(null);
                tran.setIncomeAllocation(incomeAllocationCreated);
                tran.setOwner(currentUser);
                tran.setSavingAllocation(null);
                transactionService.saveTransaction(tran);
            }
            else
            {
                // Add income to the recurrence
                Recurrence recurrence = new Recurrence();
                recurrence.setOwner(currentUser);
                recurrence.setAccount(account.get());
                recurrence.setExpense(null);
                recurrence.setIncome(incomeCreated);
                recurrence.setCreatedAt(new Date());
                recurrence.setDeleted(false);
                recurrenceRepository.save(recurrence);
            }
        }

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

    @PostMapping("/add-to-account")
    public void addIncomeToAccount(@RequestBody Income income) throws Exception {
        Optional<Account> account = accountRepository.findById(income.getAccount().getId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
        }

        Optional<Income> existingIncome = incomeRepository.findById(income.getId());
        if (existingIncome.isEmpty()) {
            throw new IllegalArgumentException("Ingreso no encontrado o no pertenece al usuario actual.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        if (existingIncome.get().getType().equals(IncomeExpenceType.UNIQUE)) {
            // Associate the income with the account
            IncomeAllocation incomeAllocation = new IncomeAllocation();
            incomeAllocation.setAccount(account.get());
            incomeAllocation.setIncome(existingIncome.get());
            incomeAllocation.setOwner(currentUser);
            incomeAllocation.setPercentage(new BigDecimal(100));
            incomeAllocation.setCreatedAt(new Date());
            incomeAllocation.setUpdatedAt(new Date());
            incomeAllocation.setDeleted(false);
            var incomeAllocationCreated = incomeAllocationRepository.save(incomeAllocation);

            // Create a transaction for the income
            var tran = new Transaction();
            tran.setAmount(existingIncome.get().getAmount());
            tran.setAccount(account.get());
            tran.setCreatedAt(new Date());
            tran.setDeletedAt(null);
            tran.setDescription("Ingreso: " + existingIncome.get().getName());
            tran.setDeleted(false);
            tran.setPreviousBalance(new BigDecimal(0));
            tran.setType(TransactionTypeEnum.INCOME);
            tran.setUpdatedAt(null);
            tran.setIncomeAllocation(incomeAllocationCreated);
            tran.setOwner(currentUser);
            tran.setSavingAllocation(null);
            transactionService.saveTransaction(tran);
        }
        else {
            // Add income to the recurrence
            Recurrence recurrence = new Recurrence();
            recurrence.setOwner(currentUser);
            recurrence.setAccount(account.get());
            recurrence.setExpense(null);
            recurrence.setIncome(existingIncome.get());
            recurrence.setCreatedAt(new Date());
            recurrence.setDeleted(false);
            recurrenceRepository.save(recurrence);
        }
    }
}