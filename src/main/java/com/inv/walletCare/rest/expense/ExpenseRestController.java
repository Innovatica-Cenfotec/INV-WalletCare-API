package com.inv.walletCare.rest.expense;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.accountUser.AccountUserRespository;
import com.inv.walletCare.logic.entity.email.EmailSenderService;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.expense.ExpenseRepository;
import com.inv.walletCare.logic.entity.helpers.Helper;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.account.AccountTypeEnum;
import com.inv.walletCare.logic.entity.accountUser.AccountUser;
import com.inv.walletCare.logic.entity.email.Email;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/expenses")
public class ExpenseRestController {

    private final ExpenseRepository expenseRepository;
    private final AccountRepository accountRepository;
    private final TaxRepository taxRepository;
    private final TransactionService transactionService;
    private final RecurrenceRepository recurrenceRepository;
    private final EmailSenderService emailSenderService;
    private final AccountUserRespository accountUserRespository;

    public ExpenseRestController(ExpenseRepository expenseRepository,
                                 AccountRepository accountRepository,
                                 TaxRepository taxRepository,
                                 TransactionService transactionService,
                                 RecurrenceRepository recurrenceRepository,
                                 EmailSenderService emailSenderService,
                                 AccountUserRespository accountUserRespository) {
        this.expenseRepository = expenseRepository;
        this.accountRepository = accountRepository;
        this.taxRepository = taxRepository;
        this.transactionService = transactionService;
        this.recurrenceRepository = recurrenceRepository;
        this.emailSenderService = emailSenderService;
        this.accountUserRespository = accountUserRespository;
    }

    /**
     *
     * @return details
     */
    @GetMapping
    public List<Expense> getExpenses() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return expenseRepository.findAllByUserId(user.getId());
    }

    /**
     *
     * @param account details
     * @return details
     */
    @GetMapping("/filter")
    public List<Expense> getExpensesByAccount(@RequestParam long account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        authentication.getPrincipal();
        return expenseRepository.findByAccount(account);
    }

    /**
     *
     * @param id details
     * @return details
     */
    @GetMapping("/{id}")
    public Expense getExpenseById(@PathVariable long id) {
        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            throw new IllegalArgumentException("Gasto no encontrado o no pertenece al usuario actual");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (!user.getId().equals(expense.get().getOwner().getId())) {

            throw new IllegalArgumentException("Gasto no encontrado o no pertenece al usuario actual");
        }
        return expense.get();
    }

    /**
     *
     * @param expense details
     * @return details
     * @throws Exception details
     */
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

            if (!user.getId().equals(tax.get().getOwner().getId())) {

                throw new FieldValidationException("tax", "El impuesto con el ID " + expense.getTax().getId() + " no existe o no pertenece al usuario actual.");
            }

            if (expense.getFrequency() == null) {
                throw new FieldValidationException("frequency", "La frecuencia es requerida para los ingresos relacionados con impuestos.");
            }

            if (expense.getFrequency() == FrequencyTypeEnum.OTHER && expense.getScheduledDay() <= 1 || expense.getScheduledDay() >= 31) {
                throw new FieldValidationException("scheduleDay", "El día programado es requerido para los ingresos relacionados con impuestos.");
            }
        }

        Optional<Account> account = accountRepository.findById(expense.getAccount().getId());
        if (account.isEmpty()) {
            throw new FieldValidationException("account", "La cuenta con el ID " + expense.getAccount().getId() + " no existe en el sistema.");
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
            account = accountRepository.findById(expense.getAccount().getId());
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

    /**
     * Add existing expense to user account.
     * @param expense Expense to add.
     * @throws Exception details
     */
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

    /**
     * Updates an existing expense with new details.
     * @param id      The ID of the expense to update.
     * @param expense An expense object containing the new values for name and description.
     * @return The updated expense object.
     * @throws RuntimeException if the expense with the specified ID is not found or not owned by the current user.
     */
    @PutMapping("/{id}")
    public Expense updateExpense(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody Expense expense) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Expense> existingExpense = expenseRepository.findById(id);
        if (existingExpense.isEmpty()) {
            throw new IllegalArgumentException("El gasto no existe o no tiene los permisos para modificarlo.");
        }

        if (existingExpense.get().getAccount() != null) {
            sendEmailToAllMembers(existingExpense.get().getAccount().getId());
        }

        // Expense details
        existingExpense.get().setOwner(currentUser); // To know who updated it
        existingExpense.get().setAccount(expense.getAccount());
        existingExpense.get().setTemplate(expense.isTemplate());
        existingExpense.get().setType(expense.getType());
        existingExpense.get().setName(expense.getName());
        existingExpense.get().setDescription(expense.getDescription());
        existingExpense.get().setAmount(expense.getAmount());
        existingExpense.get().setAmountType(expense.getAmountType());
        existingExpense.get().setFrequency(expense.getFrequency());
        existingExpense.get().setScheduledDay(expense.getScheduledDay());
        // Category and tax details
        existingExpense.get().setExpenseCategory(expense.getExpenseCategory());
        existingExpense.get().setTaxRelated(expense.isTaxRelated());
        existingExpense.get().setTax(expense.getTax());
        // Timestamps
        existingExpense.get().setUpdatedAt(new Date());
        return expenseRepository.save(existingExpense.get());
    }

    /**
     * Deletes an expense by its ID for the currently authenticated user.
     * @param id The ID of the expense to delete.
     * @throws RuntimeException if the expense is not found or not owned by the current user.
     */
    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Expense> expense = expenseRepository.findById(id);
        if (expense.isEmpty()) {
            throw new IllegalArgumentException("El gasto no existe o no tiene los permisos para eliminarlo.");
        }

        if (expense.get().getAccount() != null) {
            sendEmailToAllMembers(expense.get().getAccount().getId());
        }

        expense.get().setDeleted(true);
        expense.get().setOwner(currentUser); // To know who deleted it
        expense.get().setUpdatedAt(new Date());
        expense.get().setDeletedAt(new Date());
        expenseRepository.save(expense.get());
    }

    /**
     * Check if an account is shared, then send an email to all members with the update template.
     * @param accountId Long value with the account id.
     */
    private void sendEmailToAllMembers(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no existe.");
        }

        // Checks if the account is shared and notifies all members.
        if (account.get().getType() == AccountTypeEnum.SHARED) {
            Optional<List<AccountUser>> accountMembers = accountUserRespository.findAllByAccountID(account.get().getId());

            if (accountMembers.isPresent()) {
                // Send email parallelly to all members
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (AccountUser member : accountMembers.get()) {
                    futures.add(CompletableFuture.runAsync(() -> {
                        try {
                            Email emailDetails = new Email();
                            emailDetails.setTo(member.getUser().getEmail());
                            emailDetails.setSubject("Actualización de Cuenta Compartida");
                            Map<String, String> params = new HashMap<>();
                            params.put("accountOwnerName", account.get().getOwner().getName());
                            params.put("memberName", member.getUser().getName());
                            params.put("accountName", account.get().getName());
                            emailSenderService.sendEmail(emailDetails, "UpdateSharedAccount", params);
                        } catch (MailException e) {
                            // Log and continue with the next user
                            System.err.println("Error sending email: " + e.getMessage());
                        } catch (Exception e) {
                            throw new RuntimeException("Error al enviar la notificación de actualización de cuenta compartida.", e);
                        }
                    }));
                }
                // Wait for all to complete
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            }
        }
    }
}