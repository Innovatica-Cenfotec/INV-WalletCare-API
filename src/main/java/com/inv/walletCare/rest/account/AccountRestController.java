package com.inv.walletCare.rest.account;

import com.inv.walletCare.logic.entity.Response;
import com.inv.walletCare.logic.entity.account.*;
import com.inv.walletCare.logic.entity.accountUser.AccountUser;
import com.inv.walletCare.logic.entity.accountUser.AccountUserRespository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Controller for account-related operations.
 * Handles HTTP requests for account management within the application.
 */
@RestController
@RequestMapping("/accounts")
public class AccountRestController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private AccountUserRespository accountUserRepository;

    /**
     * Retrieves a list of {@link Account} objects associated with the currently authenticated user.
     *
     * @return a {@link List} of {@link Account} objects belonging to the currently authenticated user.
     * If no accounts are found, an empty list will be returned.
     */
    @GetMapping
    public List<Account> getAccountsbyOwner() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Retrieve the accounts for the current user
        List<Account> accounts = accountRepository.findAllByOwnerId(currentUser.getId()).get();

        // Retrieve the inactive accounts for the current user
        accountUserRepository.findAllByUserId(currentUser.getId()).ifPresent(accountUser -> {
            if(accountUser.getInvitationStatus() == 2){
                accounts.add(accountUser.getAccount());
            }
        });

        return accounts;
    }

    /**
     * Retrieves an account by its ID for the currently authenticated user.
     *
     * @param id The ID of the account to retrieve.
     * @return The {@link Account} object corresponding to the specified ID.
     * @throws RuntimeException if the account is not found or not owned by the current user.
     */
    @GetMapping("/{id}")
    public Account getAccountById(@PathVariable Long id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Check if the account belongs to the current user
        if (currentUser.getId().equals(account.get().getOwner().getId())) {
            return account.get();
        }

        // Check if the account is shared with the current user
        if (accountUserRepository.findByUserIdAndAccountId(id, currentUser.getId()).isPresent()) {
            return account.get();
        }

        throw new RuntimeException("La cuenta no se encontró o no pertenece al usuario actual.");
    }

    /**
     * Creates a new account with the provided details.
     * Validates the uniqueness of the account name for the user before creation.
     *
     * @param account The account details from the request body, validated against the OnCreate group.
     * @return The saved account entity.
     * @throws FieldValidationException if the account name is already in use by the user.
     */
    @PostMapping
    public Account AddAccount(@Validated(OnCreate.class) @RequestBody Account account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Validate that the account name is unique for the user
        var existingAccount = accountRepository.findByNameAndOwnerId(account.getName(), currentUser.getId());
        if (existingAccount.isPresent()) {
            throw new FieldValidationException("name", "El nombre de la cuenta que has elegido ya está en uso. Por favor, ingresa uno diferente");
        }

        Account newAccount = new Account();
        newAccount.setName(account.getName());
        newAccount.setDescription(account.getDescription());
        newAccount.setOwner(currentUser);
        newAccount.setType(account.getType());
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCreatedAt(new Date());
        newAccount.setUpdatedAt(new Date());
        newAccount.setDeleted(false);
        newAccount.setDefault(false);
        return accountRepository.save(newAccount);
    }

    /**
     * Deletes an account by its ID for the currently authenticated user.
     *
     * @param id The ID of the account to delete.
     * @throws RuntimeException if the account is not found or not owned by the current user.
     */
    @DeleteMapping("/{id}")
    public void deleteAccount(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Account> account = accountRepository.findByIdAndOwnerId(id, currentUser.getId());
        if (account.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }

        if (account.get().isDefault()) {
            throw new IllegalArgumentException("No se puede eliminar la cuenta predeterminada.");
        }

        if (account.get().getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("No se puede eliminar una cuenta con saldo distinto de cero.");
        }

        account.get().setDeleted(true);
        account.get().setUpdatedAt(new Date());
        account.get().setDeletedAt(new Date());
        accountRepository.save(account.get());
    }

    /**
     * Updates an existing account with new details.
     *
     * @param id      The ID of the account to update.
     * @param account An account object containing the new values for name and description.
     * @return The updated account object.
     * @throws RuntimeException if the account with the specified ID is not found or not owned by the current user.
     */
    @PutMapping("/{id}")
    public Account updateAccount(@Validated(OnUpdate.class) @PathVariable Long id,@RequestBody Account account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Account> existingAccount = accountRepository.findByIdAndOwnerId(id, currentUser.getId());
        if (existingAccount.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }

        existingAccount.get().setUpdatedAt(new Date());
        existingAccount.get().setName(account.getName());
        existingAccount.get().setDescription(account.getDescription());
        return accountRepository.save(existingAccount.get());
    }

    @GetMapping("/members/{id}")
    public List<AccountUser> getMembers(@PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Account> account = accountRepository.findById(id);
        if (account.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }

        if (account.get().getType() == AccountTypeEnum.PERSONAL) {
            throw new IllegalArgumentException("Las cuentas personales no tienen miembros.");
        }

        Optional<List<AccountUser>> accountUsers = accountUserRepository.findAllByAccountID(id);
        if (accountUsers.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }

        // if the current user is the owner of the account, return all users
        if (currentUser.getId().equals(account.get().getOwner().getId())) {
            return accountUsers.get();
        }

        // if the current user is a member of the account, return all users
        if (accountUsers.get().stream().anyMatch(accountUser -> currentUser.getId().equals(accountUser.getUser().getId()))) {
            return accountUsers.get();
        }

        throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
    }

    @PutMapping("/invitation/{id}")
    public ResponseEntity<Response> manageSharedAccounInvitationtStatus(@Validated(OnUpdate.class) @PathVariable Long id , @RequestBody AccountUser accountUser){
        var acUser = accountUserRepository.findByUserIdAndAccountId(id, accountUser.getUser().getId());
        var gResponse = new Response();
        if(acUser.isEmpty()){
            throw new ValidationException("No se ha encontrado la invitación a la cuenta compartida indicada, favor solicita la invitación de nuevo.");
        }else {
            switch (acUser.get().getInvitationStatus()){
                case 1:
                    acUser.map(existingAccount ->{

                        existingAccount.setInvitationStatus(accountUser.getInvitationStatus());
                        if(accountUser.getInvitationStatus() == 2){
                            gResponse.setMessage("La invitación se aceptó correctamente, revisa tus cuentas para poder ver su información.");
                            existingAccount.setJoinedAt(new Date());
                        } else if (accountUser.getInvitationStatus() == 3) {
                            gResponse.setMessage("La invitación se rechazó correctamente.");
                        }
                        return accountUserRepository.save(existingAccount);
                    });

                    break;
                case 2:
                    throw new ValidationException("Esta invitación ya fue aceptada con anterioridad, revisa tus cuentas para poder ver su información.");
                case 3:
                    throw new ValidationException("Esta invitación ya fue rechazada con anterioridad, solicita que te inviten de nuevo.");
                default:
                    throw new ValidationException("El estado de la invitación no es reconocido por el sistema,  solicita que te inviten de nuevo.");
            }
        }
        return ResponseEntity.ok(gResponse);
    }
}