package com.inv.walletCare.rest.income;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.income.IncomeRepository;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocation;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocationRepository;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.report.BarchartDTO;
import com.inv.walletCare.logic.entity.report.ReportService;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxRepository;
import com.inv.walletCare.logic.entity.transaction.Transaction;
import com.inv.walletCare.logic.entity.transaction.TransactionService;
import com.inv.walletCare.logic.entity.transaction.TransactionTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
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

    private final IncomeRepository incomeRepository;

    private final AccountRepository accountRepository;

    private final TaxRepository taxRepository;

    private final TransactionService transactionService;

    private final IncomeAllocationRepository incomeAllocationRepository;

    private final RecurrenceRepository recurrenceRepository;

    private final ReportService reportService;

    public IncomeRestController(IncomeRepository incomeRepository,
                                AccountRepository accountRepository,
                                TaxRepository taxRepository,
                                TransactionService transactionService,
                                IncomeAllocationRepository incomeAllocationRepository,
                                RecurrenceRepository recurrenceRepository,
                                ReportService reportService) {
        this.incomeRepository = incomeRepository;
        this.accountRepository = accountRepository;
        this.taxRepository = taxRepository;
        this.transactionService = transactionService;
        this.incomeAllocationRepository = incomeAllocationRepository;
        this.recurrenceRepository = recurrenceRepository;
        this.reportService = reportService;
    }

    @GetMapping
    public List<Income> getIncomes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return incomeRepository.findAllByOwnerId(currentUser.getId());
    }

    @PostMapping
    public Income addIncome(@Validated(OnCreate.class) @RequestBody Income income) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Validate that the income name is unique for the user
        Optional<Income> existingIncome = incomeRepository.findByNameAndOwnerIdAAndTemplate(income.getName(), currentUser.getId());
        if (existingIncome.isPresent() && income.isTemplate()) {
            throw new FieldValidationException("name",
                    "El nombre del ingreso que ha ingresado ya está en uso. Por favor, ingresa uno diferente.");
        }

        // If it has a tax, validate and add it.
        Tax tax = null;
        if (income.getTax() != null) {
            var taxExists = taxRepository.findByIdAndUserId(income.getTax().getId(), currentUser.getId());
            if (taxExists.isEmpty()) {
                throw new IllegalArgumentException("Impuesto no encontrado.");
            }

            tax = taxExists.get();
        }

        Income newIncome = new Income();
        newIncome.setName(income.getName());
        newIncome.setAmount(income.getAmount());
        newIncome.setDescription(income.getDescription());
        newIncome.setOwner(currentUser);
        newIncome.setTemplate(income.isTemplate());
        newIncome.setAmountType(income.getAmountType());
        newIncome.setFrequency(income.getFrequency());
        newIncome.setScheduledDay(income.getScheduledDay());
        newIncome.setTaxRelated(income.isTaxRelated());
        newIncome.setTax(tax);
        newIncome.setTaxRelated(tax != null);
        newIncome.setCreatedAt(new Date());
        newIncome.setUpdatedAt(new Date());
        newIncome.setDeleted(false);
        newIncome.setType(income.getType());
        newIncome.setDescription(income.getDescription());
        var incomeCreated = incomeRepository.save(newIncome);

        if (income.isAddTransaction()){
            Optional<Account> account = accountRepository.findById(income.getAccount().getId());
            if (account.isEmpty()) {
                throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
            }

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

            if (incomeCreated.getType().equals(IncomeExpenceType.UNIQUE)) {
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

    /**
     * Get all expenses with isTemplate = true created by logged user.
     * @return List of expenses templates created by logged user.
     */
    @GetMapping("/templates")
    public List<Income> getExpenseTemplatesByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return incomeRepository.findAllTemplatesByUserId(user.getId()).get();
    }

    @PutMapping("/{id}")
    public Income updateIncome(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody Income income) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<Income> existingIncome = incomeRepository.findByIdAndUserId(id, currentUser.getId());
        if (existingIncome.isEmpty()) {
            throw new IllegalArgumentException("El ingreso no se encontró o no pertenece al usuario actual");
        }
        var existingIncomeName = incomeRepository.findByNameAndOwnerIdAAndTemplate(income.getName(), currentUser.getId());
        if (existingIncomeName.isPresent() && existingIncome.get().getId() != existingIncomeName.get().getId()) {
            throw new FieldValidationException("name", "El nombre del ingreso que ha ingresado ya está en uso. Por favor, ingresa uno diferente.");
        }

        // If it has a tax, validate and add it.
        Tax tax = null;
        if (income.getTax() != null) {
            var taxExists = taxRepository.findByIdAndUserId(income.getTax().getId(), currentUser.getId());
            if (taxExists.isEmpty()) {
                throw new IllegalArgumentException("Impuesto no encontrado.");
            }

            tax = taxExists.get();
        }

        //if (existingIncome.get().getType() == IncomeExpenceType.RECURRENCE){}
        existingIncome.get().setUpdatedAt(new Date());
        existingIncome.get().setName(income.getName());
        existingIncome.get().setDescription(income.getDescription());
        existingIncome.get().setAmount(income.getAmount());
        existingIncome.get().setAmountType(income.getAmountType());
        existingIncome.get().setTax(income.getTax());
        existingIncome.get().setFrequency(income.getFrequency());
        existingIncome.get().setTax(tax);
        existingIncome.get().setTaxRelated(tax != null);
        return  incomeRepository.save(existingIncome.get());
    }

    @DeleteMapping("/{id}")
    public void deleteIncome(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<Income> existingIncome = incomeRepository.findByIdAndUserId(id, currentUser.getId());
        if (existingIncome.get().getOwner().getId()!=currentUser.getId()) {
            throw new IllegalArgumentException("No eres el propietario de esta cuenta, no puedes eliminarla.");
        }
        existingIncome.get().setDeletedAt(new Date());
        existingIncome.get().setDeleted(true);
        existingIncome.get().setUpdatedAt(new Date());
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

    /**
     * Get a report of incomes sort by month and category created by logged user.
     * @return List of BarchartDTO with the report of income.
     */
    @GetMapping("/report/by-category/{year}")
    public List<BarchartDTO> getAnualAmountByCategory(@PathVariable int year) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return reportService.getYearlyIncomeByCategoryReport(year, user.getId());
    }
}
