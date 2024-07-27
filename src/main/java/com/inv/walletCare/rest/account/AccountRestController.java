package com.inv.walletCare.rest.account;

import com.inv.walletCare.logic.entity.Response;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.account.AccountTypeEnum;
import com.inv.walletCare.logic.entity.accountUser.AccountUser;
import com.inv.walletCare.logic.entity.accountUser.AccountUserRespository;
import com.inv.walletCare.logic.entity.email.Email;
import com.inv.walletCare.logic.entity.email.EmailSenderService;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;

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

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;

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
            if (accountUser.getInvitationStatus() == 2) {
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
        if (account.get().getOwner().getId() == currentUser.getId()) {
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
    public Account updateAccount(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody Account account) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        Optional<Account> existingAccount = accountRepository.findByIdAndOwnerId(id, currentUser.getId());
        if (existingAccount.isEmpty()) {
            throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
        }
        
        // Validate that the account name is unique for the user
        var existingAccountName = accountRepository.findByNameAndOwnerId(account.getName(), currentUser.getId());
        if (existingAccountName.isPresent()) {
            throw new FieldValidationException("name", "El nombre de la cuenta que has elegido ya está en uso. Por favor, ingresa uno diferente");
        }
        
        // Checks if the account is shared and notifies all members.
        if (existingAccount.get().getType() == AccountTypeEnum.SHARED) {
            Optional<List<AccountUser>> accountUsers = accountUserRepository.findAllByAccountID(id);
            
            if (accountUsers.isPresent()) {
                // Send email parallelly to all members
                List<CompletableFuture<Void>> futures = new ArrayList<>();
                for (AccountUser accountUser : accountUsers.get()) {
                    futures.add(CompletableFuture.runAsync(() -> {
                        try {
                            Email emailDetails = new Email();
                            emailDetails.setTo(accountUser.getUser().getEmail());
                            emailDetails.setSubject("Actualización de Cuenta Compartida");
                            Map<String, String> params = new HashMap<>();
                            params.put("accountOwnerName", currentUser.getEmail());
                            params.put("memberName", accountUser.getUser().getEmail());
                            params.put("accountName", existingAccount.get().getName());
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

        existingAccount.get().setUpdatedAt(new Date());
        existingAccount.get().setName(account.getName());
        existingAccount.get().setDescription(account.getDescription());
        return accountRepository.save(existingAccount.get());
    }

    /**
     * Retreives the list of all members of the shared account
     *
     * @param id is the account id
     * @return returns the list of all members of the shared account
     */
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
        if (account.get().getOwner().getId() == currentUser.getId()) {
            return accountUsers.get();
        }

        // if the current user is a member of the account, return all users
        if (accountUsers.get().stream().anyMatch(accountUser -> accountUser.getUser().getId() == currentUser.getId())) {
            return accountUsers.get();
        }

        throw new IllegalArgumentException("La cuenta no se encontró o no pertenece al usuario actual.");
    }

    /**
     * This cotroller send the invitation to shared account
     *
     * @param accountUser is the invitation of the shared account
     * @throws Exception handles the all validation exceptions
     */
    @PostMapping("/inviteToSharedAccount")
    public AccountUser inviteToSharedAccount(@RequestBody AccountUser accountUser) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        Optional<User> invitedUser = userRepository.findByEmail(accountUser.getUser().getEmail());
        if (invitedUser.isEmpty()) {
            throw new Exception("El usuario que ha intentado invitar no existe");
        }

        if (currentUser.getId() == invitedUser.get().getId()) {
            throw new Exception("No puedes invitarte a ti mismo a una cuenta compartida");
        }

        AccountUser newAccountUser = null;
        Optional<AccountUser> sharedAccount = accountUserRepository.findByUserIdAndAccountId(accountUser.getAccount().getId(), invitedUser.get().getId());
        Optional<Account> baseAccount = accountRepository.findById(accountUser.getAccount().getId());
        if (sharedAccount.isEmpty()) {
            newAccountUser = new AccountUser();
            newAccountUser.setAccount(accountRepository.findById(accountUser.getAccount().getId()).get());
            newAccountUser.setUser(invitedUser.get());
            newAccountUser.setActive(true);
            newAccountUser.setDeleted(false);
            newAccountUser.setInvitationStatus(1);
            accountUserRepository.save(newAccountUser);
        } else {
            if (sharedAccount.get().getInvitationStatus() == 1) {
                throw new Exception("La invitación ya ha sido enviada a este usuario");
            }

            if (sharedAccount.get().getInvitationStatus() == 2) {
                throw new Exception("El usuario ya forma parte de esta cuenta");
            }

            if (sharedAccount.get().getInvitationStatus() == 3) {
                throw new Exception("El usuario ha rechazado la invitación a esta cuenta");
            }
        }

        Email emailDetails = new Email();
        emailDetails.setTo(accountUser.getUser().getEmail());
        emailDetails.setSubject("Invitación a Cuenta Compartida");
        Map<String, String> params = new HashMap<>();
        params.put("accountOwner", currentUser.getEmail());
        params.put("invitedUser", accountUser.getUser().getEmail());
        params.put("accountName", baseAccount.get().getName());
        params.put("invitationHandlerLink",
                "http://localhost:4200/invitation" +
                        "?host=" + baseAccount.get().getOwner().getEmail() +
                        "&accountName=" + baseAccount.get().getName() +
                        "&accountId=" + baseAccount.get().getId() +
                        "&userId=" + invitedUser.get().getId());
        emailSenderService.sendEmail(emailDetails, "InviteToShareAccount", params);
        return newAccountUser;
    }

    /**
     * This controller handles the invitattion status to the shared account
     *
     * @param id          is the account id
     * @param accountUser is the invitation with the status
     * @return returns a message with the status of the invitation
     */
    @PutMapping("/invitation/{id}")
    public ResponseEntity<Response> manageSharedAccounInvitationtStatus(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody AccountUser accountUser) {
        var acUser = accountUserRepository.findByUserIdAndAccountId(id, accountUser.getUser().getId());
        var gResponse = new Response();
        if (acUser.isEmpty()) {
            throw new ValidationException("No se ha encontrado la invitación a la cuenta compartida indicada, favor solicita la invitación de nuevo.");
        } else {
            switch (acUser.get().getInvitationStatus()) {
                case 1:
                    acUser.map(existingAccount -> {

                        existingAccount.setInvitationStatus(accountUser.getInvitationStatus());
                        if (accountUser.getInvitationStatus() == 2) {
                            gResponse.setMessage("La invitación se aceptó correctamente, revisa tus cuentas para poder ver su información.");
                            existingAccount.setJoinedAt(new Date());
                        } else if (accountUser.getInvitationStatus() == 3) {
                            gResponse.setMessage("La invitación se rechazó correctamente.");
                        } else {
                            throw new ValidationException("El estado de la invitación seleccionado no fue reconocido, favor intentntalo nuevamente. ");
                        }
                        return accountUserRepository.save(existingAccount);
                    });

                    break;
                case 2:
                    throw new ValidationException("Esta invitación ya fue aceptada con anterioridad, revisa tus cuentas para poder ver su información.");
                case 3:
                    throw new ValidationException("Esta invitación ya fue rechazada con anterioridad, solicita que te inviten de nuevo.");
                case 4:
                    throw new ValidationException("Ya te habías salido de esta cuenta compartida, si quieres entrar nuevamente solicita al dueño una nueva invitación.");
                default:
                    throw new ValidationException("El estado de la invitación no es reconocido por el sistema,  solicita que la inviten de nuevo.");
            }
        }
        return ResponseEntity.ok(gResponse);
    }

    /**
     * @param id
     * @param accountUser
     * @return
     */
    @PutMapping("/leave-account/{id}")
    public ResponseEntity<Response> leaveSharedAccount(@Validated(OnUpdate.class) @PathVariable Long id, @RequestBody AccountUser accountUser) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        var sharedAccount = accountUserRepository.findByUserIdAndAccountId(id, accountUser.getUser().getId());
        var account = accountRepository.findById(sharedAccount.get().getAccount().getId());

        String message;
        if (sharedAccount.isEmpty()) {
            throw new ValidationException("No se ha encontrado la cuenta compartida indicada, favor intentalo de nuevo.");
        }

        switch (sharedAccount.get().getInvitationStatus()) {
            case 2:
                sharedAccount.map(existingAccount -> {
                    existingAccount.setInvitationStatus(4);
                    existingAccount.setLeftAt(new Date());
                    existingAccount.setActive(false);
                    return accountUserRepository.save(existingAccount);
                });
                break;
            case 3:
                throw new ValidationException("Esta invitación a esta cuenta compartida ya fue rechazada con anterioridad, así que no formas parte de la misma.");
            case 4:
                throw new ValidationException("Ya te habías salido de esta cuenta compartida, así que no formas parte de la misma.");
            default:
                throw new ValidationException("Tu estado en esta cuenta compartida no esta reconocido por el sistema, intenta esta acción mas tarde.");

        }
        var mail = new Email();
        mail.setSubject("Notificación de salida de cuenta compartida.");

        var params = new HashMap<String, String>();
        params.put("accountOwner", account.get().getOwner().getEmail());
        params.put("accountMember", sharedAccount.get().getUser().getEmail());
        params.put("accountName", account.get().getName());


        //The memeber is leaving the shared account
        if (Objects.equals(currentUser.getId(), accountUser.getUser().getId())) {
            mail.setTo(sharedAccount.get().getAccount().getOwner().getEmail());
            emailSenderService.sendEmail(mail, "LeaveSharedAccount", params);
            message = "Te has salido correctamente de la cuenta compartida, recuerda que ahora todos los gastos, ingresos y ahorros pasan a tu cuenta principal.";
        } else {
            //The Owner remove the memeber from the shared account
            mail.setTo(sharedAccount.get().getUser().getEmail());
            emailSenderService.sendEmail(mail, "RemoveFromSharedAccount", params);
            message = "Se ha eliminado correctamente al usuario " + sharedAccount.get().getUser().getEmail() + "de la cuenta compartida. ";
        }
        return ResponseEntity.ok(new Response(message));
    }
}