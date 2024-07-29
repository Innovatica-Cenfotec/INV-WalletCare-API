package com.inv.walletCare.rest.expense;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expense.ExpenseRepository;
import com.inv.walletCare.logic.entity.helpers.Helper;
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
@RequestMapping("/expenses")
public class ExpenseRestController {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TaxRepository taxRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @GetMapping
    public List<Expense> getExpenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseRepository.findAllByUserId(user.getId());
    }

    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado o no pertenece al usuario actual");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (expense.get().getOwner().getId() != user.getId()) {
            throw new IllegalArgumentException("Gasto no encontrado o no pertenece al usuario actual");
        }

        return expense.get();
    }

    @PostMapping
    public Expense createExpense(@Validated(OnCreate.class) @RequestBody Expense expense) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Optional<Expense> existingExpense = expenseRepository.findByNameAndOwnerId(expense.getName(), user.getId());
        if (existingExpense.isPresent()) {
            throw new FieldValidationException("name", "El nombre del gasto que ha ingresado ya está en uso. Por favor, ingrese uno diferente.");
        }

        if (expense.isTaxRelated()) {
            Optional<Tax> tax = taxRepository.findById(expense.getTax().getId());
            if (tax.isEmpty()) {
                throw new FieldValidationException("tax", "El impuesto es requerido para los gastos relacionados con impuestos.");
            }

            if (tax.get().getOwner().getId() != user.getId()) {
                throw new FieldValidationException("tax", "El impuesto con el ID " + expense.getTax().getId() + " no existe o no pertenece al usuario actual.");
            }

            if (expense.getFrequency() == null) {
                throw new FieldValidationException("frequency", "La frecuencia es requerida para los ingresos relacionados con impuestos.");
            }

            if (expense.getFrequency() == FrequencyTypeEnum.OTHER && expense.getScheduledDay() <= 1 || expense.getScheduledDay() >= 31) {
                throw new FieldValidationException("scheduleDay", "El día programado es requerido para los ingresos relacionados con impuestos.");
            }
        }

        Expense newExpense = new Expense();
        newExpense.setAccount(expense.getAccount());
        newExpense.setName(expense.getName());
        newExpense.setAmount(expense.getAmount());
        newExpense.setAmountType(expense.getAmountType());
        newExpense.setOwner(user);
        newExpense.setDescription(expense.getDescription());
        newExpense.setTemplate(expense.isTemplate());
        newExpense.setFrequency(expense.getFrequency());
        newExpense.setScheduledDay(expense.getScheduledDay());
        newExpense.setTax(expense.getTax());
        newExpense.setTaxRelated(expense.isTemplate());
        newExpense.setCreatedAt(new Date());
        newExpense.setUpdatedAt(new Date());
        newExpense.setDeleted(false);
        newExpense.setType(expense.getType());
        var expenseCreated = expenseRepository.save(newExpense);

        if (expense.isAddTransaction()) {
            Optional<Account> account = accountRepository.findById(expense.getAccount().getId());
            if (account.isEmpty()) {
                throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
            }

            if (expenseCreated.getType().equals(IncomeExpenceType.UNIQUE)) {
                var tran = new Transaction();
                tran.setAmount(Helper.reverse(expenseCreated.getAmount()));
                tran.setCreatedAt(new Date());
                tran.setDeletedAt(null);
                tran.setDescription("Gasto: " + expenseCreated.getName());
                tran.setDeleted(false);
                tran.setPreviousBalance(new BigDecimal(0));
                tran.setType(TransactionTypeEnum.EXPENSE);
                tran.setUpdatedAt(null);
                tran.setAccount(expense.getAccount());
                tran.setExpense(expenseCreated);
                tran.setIncomeAllocation(null);
                tran.setOwner(user);
                tran.setSavingAllocation(null);
                transactionService.saveTransaction(tran);

            } else {
                // Add expense to the recurrence
                Recurrence recurrence = new Recurrence();
                recurrence.setOwner(user);
                recurrence.setAccount(expense.getAccount());
                recurrence.setExpense(expenseCreated);
                recurrence.setCreatedAt(new Date());
                recurrence.setDeleted(false);
                recurrenceRepository.save(recurrence);
            }
        }

        return expenseCreated;
    }

    @PostMapping("/add-to-account")
    public void addExpenseToAccount(@RequestBody Expense expense) throws Exception {
        Optional<Account> account = accountRepository.findById(expense.getAccount().getId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
        }

        Optional<Expense> expenseCreated = expenseRepository.findById(expense.getId());
        if (expenseCreated.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado o no pertenece al usuario actual.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (expenseCreated.get().getType().equals(IncomeExpenceType.UNIQUE)) {
            var tran = new Transaction();
            tran.setAmount(Helper.reverse(expenseCreated.get().getAmount()));
            tran.setCreatedAt(new Date());
            tran.setDeletedAt(null);
            tran.setDescription("Gasto: " + expenseCreated.get().getName());
            tran.setDeleted(false);
            tran.setPreviousBalance(new BigDecimal(0));
            tran.setType(TransactionTypeEnum.EXPENSE);
            tran.setUpdatedAt(null);
            tran.setAccount(expense.getAccount());
            tran.setExpense(expenseCreated.get());
            tran.setIncomeAllocation(null);
            tran.setOwner(user);
            tran.setSavingAllocation(null);
            transactionService.saveTransaction(tran);
        } else {
            // Add expense to the recurrence
            Recurrence recurrence = new Recurrence();
            recurrence.setOwner(user);
            recurrence.setAccount(expense.getAccount());
            recurrence.setExpense(expenseCreated.get());
            recurrence.setCreatedAt(new Date());
            recurrence.setDeleted(false);
            recurrenceRepository.save(recurrence);
        }
    }
}