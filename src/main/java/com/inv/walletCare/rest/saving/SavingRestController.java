package com.inv.walletCare.rest.saving;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.helpers.Helper;
import com.inv.walletCare.logic.entity.recurrence.Recurrence;
import com.inv.walletCare.logic.entity.recurrence.RecurrenceRepository;
import com.inv.walletCare.logic.entity.saving.Saving;
import com.inv.walletCare.logic.entity.saving.SavingRepository;
import com.inv.walletCare.logic.entity.saving.SavingTypeEnum;
import com.inv.walletCare.logic.entity.savingAllocation.SavingAllocation;
import com.inv.walletCare.logic.entity.savingAllocation.SavingAllocationRepository;
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

/**
 * REST controller for managing savings.
 */
@RestController
@RequestMapping("/savings")
public class SavingRestController {

    @Autowired
    private SavingRepository savingRepository;

    @Autowired
    private SavingAllocationRepository savingAllocationRepository;

    @Autowired
    private RecurrenceRepository recurrenceRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionService transactionService;

    /**
     * Get all savings of the current authenticated user.
     *
     * @return the list of savings
     */
    @GetMapping
    public List<Saving> getSavings() {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Return the list of savings owned by the current user
        return savingRepository.findByOwnerId(currentUser.getId());
    }

    /**
     * Create a new saving.
     *
     * @param saving the saving to create
     * @return the created saving
     * @throws Exception if there is an error during creation
     */
    @PostMapping
    public Saving addSaving(@Validated(OnCreate.class) @RequestBody Saving saving) throws Exception {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the saving name already exists for the current user
        Optional<Saving> existingSaving = savingRepository.findByNameAndOwnerId(saving.getName(), currentUser.getId());
        if (existingSaving.isPresent()) {
            throw new FieldValidationException("name",
                    "El nombre del ahorro que ha ingresado ya está en uso. Por favor, ingrese uno diferente.");
        }

        // Validate the type of saving
        if (saving.getType() == null) {
            throw new FieldValidationException("type",
                    "El tipo de ahorro es requerido.");
        }

        // Validate frequency and scheduled day for recurrent savings
        if (saving.getType() == SavingTypeEnum.RECURRENCE) {
            if (saving.getFrequency() == null) {
                throw new FieldValidationException("frequency",
                        "La frecuencia es requerida para los ahorros recurrentes.");
            }

            if (saving.getFrequency() == FrequencyTypeEnum.OTHER
                    && (saving.getScheduledDay() <= 1 || saving.getScheduledDay() >= 31)) {
                throw new FieldValidationException("frequency",
                        "El día programado es requerido para los ahorros recurrentes.");
            }
        }

        // Check if the account exists and belongs to the current user
        Optional<Account> accountOpt = accountRepository.findById(saving.getAccount().getId());
        if (accountOpt.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
        }
        Account account = accountOpt.get();

        // Validate the amount is greater than 0
        if (saving.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new FieldValidationException("amount", "El monto del ahorro no puede ser negativo y debe ser mayor a 0.");
        }

        // Create and save the new saving
        Saving newSaving = new Saving();
        newSaving.setOwner(currentUser);
        newSaving.setName(saving.getName());
        newSaving.setAmount(saving.getAmount());
        newSaving.setDescription(saving.getDescription());
        newSaving.setType(saving.getType());
        newSaving.setAccount(account);
        newSaving.setCreatedAt(new Date());
        newSaving.setUpdatedAt(new Date());
        newSaving.setDeleted(false);

        if (saving.getType() == SavingTypeEnum.RECURRENCE) {
            newSaving.setFrequency(saving.getFrequency());
            newSaving.setScheduledDay(saving.getScheduledDay());
        }

        Saving savingCreated = savingRepository.save(newSaving);

        SavingAllocation savingAllocation = new SavingAllocation();
        savingAllocation.setAccount(account);
        savingAllocation.setSaving(savingCreated);
        savingAllocation.setOwner(currentUser);
        savingAllocation.setAmount(newSaving.getAmount());
        savingAllocation.setCreatedAt(new Date());
        savingAllocation.setUpdatedAt(new Date());
        savingAllocation.setDeleted(false);
        savingAllocationRepository.save(savingAllocation);

        // Create a saving allocation and a transaction if addTransaction is true
        if (saving.isAddTransaction()) {

            BigDecimal savingAmount = saving.getAmount();
            BigDecimal currentBalance = account.getBalance();
            if (currentBalance.compareTo(savingAmount) == -1) {
                throw new IllegalArgumentException("El valor no puede ser mayor al balance general de la cuenta.");
            }

            Transaction transaction = new Transaction();
            transaction.setPreviousBalance(new BigDecimal(0));
            transaction.setOwner(currentUser);
            transaction.setAccount(account);
            transaction.setAmount(Helper.reverse(saving.getAmount()));
            transaction.setCreatedAt(new Date());
            transaction.setUpdatedAt(null);
            transaction.setDescription("Ahorro: " + savingCreated.getName());
            transaction.setDeleted(false);
            transaction.setType(TransactionTypeEnum.SAVING);
            transaction.setSavingAllocation(savingAllocation);
            transactionService.saveTransaction(transaction);
        }

        // Create a recurrence record for recurrent savings
        if (saving.getType() == SavingTypeEnum.RECURRENCE) {
            Recurrence recurrence = new Recurrence();
            recurrence.setOwner(currentUser);
            recurrence.setAccount(saving.getAccount());
            recurrence.setSaving(savingCreated);
            recurrence.setCreatedAt(new Date());
            recurrence.setDeleted(false);
            recurrenceRepository.save(recurrence);
        }

        return savingCreated;
    }

    /**
     * Adds a saving to the account.
     *
     * @param saving the saving to add
     * @throws Exception if there is an error during the process
     */
    @PostMapping("/add-to-account")
    public void addSavingToAccount(@RequestBody Saving saving) throws Exception {
        // Get the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Find the account by ID and check if it exists
        Optional<Account> account = accountRepository.findById(saving.getAccount().getId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("Cuenta no encontrada o no pertenece al usuario actual.");
        }

        // Find the saving by ID and check if it exists
        Optional<Saving> existingSaving = savingRepository.findById(saving.getId());
        if (existingSaving.isEmpty()) {
            throw new IllegalArgumentException("Ahorro no encontrado o no pertenece al usuario actual.");
        }

        // If the saving type is UNIQUE, create a new saving allocation and transaction
        if (existingSaving.get().getType().equals(SavingTypeEnum.UNIQUE)) {
            // Create and save the saving allocation
            SavingAllocation savingAllocation = new SavingAllocation();
            savingAllocation.setAccount(account.get());
            savingAllocation.setSaving(existingSaving.get());
            savingAllocation.setOwner(currentUser);
            savingAllocation.setAmount(Helper.reverse(existingSaving.get().getAmount()));
            savingAllocation.setCreatedAt(new Date());
            savingAllocation.setUpdatedAt(new Date());
            savingAllocation.setDeleted(false);
            var savingAllocationCreated = savingAllocationRepository.save(savingAllocation);

            // Create and save the transaction
            var transaction = new Transaction();
            transaction.setAmount(existingSaving.get().getAmount());
            transaction.setAccount(account.get());
            transaction.setCreatedAt(new Date());
            transaction.setUpdatedAt(null);
            transaction.setDescription("Ahorro: " + existingSaving.get().getName());
            transaction.setDeleted(false);
            transaction.setType(TransactionTypeEnum.SAVING);
            transaction.setSavingAllocation(savingAllocation);
            transaction.setPreviousBalance(new BigDecimal(0));
            transaction.setOwner(currentUser);
            transaction.setIncomeAllocation(null);
            transaction.setExpense(null);
            transactionService.saveTransaction(transaction);
        } else {
            // If the saving type is not UNIQUE, create a new recurrence
            Recurrence recurrence = new Recurrence();
            recurrence.setOwner(currentUser);
            recurrence.setAccount(account.get());
            recurrence.setIncome(null);
            recurrence.setExpense(null);
            recurrence.setSaving(existingSaving.get());
            recurrence.setCreatedAt(new Date());
            recurrence.setDeleted(false);
            recurrenceRepository.save(recurrence);
        }
    }
    @DeleteMapping("/{id}")
    public void deleteSaving(@PathVariable Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<Saving> existingSaving = savingRepository.findByIdAndUserId(id, currentUser.getId());
        if (existingSaving.get().getOwner().getId()!=currentUser.getId()) {
            throw new IllegalArgumentException("No eres el propietario de esta cuenta, no puedes eliminarla.");
        }
        existingSaving.get().setDeletedAt(new Date());
        existingSaving.get().setDeleted(true);
        existingSaving.get().setUpdatedAt(new Date());
        savingRepository.save(existingSaving.get());
    }

    /**
     * Update an existing saving.
     *
     * @param id     the ID of the saving to update
     * @param saving the saving data to update
     * @return the updated saving
     */
    @PutMapping("/{id}")
    public Saving updateSaving(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody Saving saving) {
        // Retrieve the currently authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Find the saving by ID and user ID, check if it exists
        Optional<Saving> existingSaving = savingRepository.findByIdAndUserId(id, currentUser.getId());
        if (existingSaving.isEmpty()) {
            throw new IllegalArgumentException("El ahorro no se encontró no pertenece al usuario actual.");
        }

        // Check if the new saving name is already in use by another saving of the same user
        var existingSavingName = savingRepository.findByNameAndOwnerId(saving.getName(), currentUser.getId());
        if (existingSavingName.isPresent() && existingSaving.get().getId() != existingSavingName.get().getId()) {
            throw new FieldValidationException("name", "El nombre del ahorro que ha ingresado ya esta en uso. Por favor, ingrese uno diferente.");
        }

        // Update the saving details
        existingSaving.get().setUpdatedAt(new Date());
        existingSaving.get().setName(saving.getName());
        existingSaving.get().setDescription(saving.getDescription());

        // Save and return the updated saving
        return savingRepository.save(existingSaving.get());
    }
}
