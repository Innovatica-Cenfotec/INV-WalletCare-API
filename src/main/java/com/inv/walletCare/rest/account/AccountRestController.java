package com.inv.walletCare.rest.account;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.email.Email;
import com.inv.walletCare.logic.entity.email.EmailSenderService;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.rest.user.UserRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private UserRestController userRestController;
    @Autowired
    private EmailSenderService emailSenderService;

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

        return accountRepository.save(newAccount);
    }

    /**
     * Retrieves a list of {@link Account} objects associated with the currently authenticated user.
     * @return a {@link List} of {@link Account} objects belonging to the currently authenticated user.
     * If no accounts are found, an empty list will be returned.
     */
    @GetMapping
    public List<Account> getAccountsbyOwner(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();
        return accountRepository.findAllByOwnerId(currentUser.getId()).get();
    }
    @PostMapping ("/inviteToSharedAccount")
    public void inviteToSharedAccount(String inviteUser) throws Exception{
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User currentUser= (User) authentication.getPrincipal();

        if(userRestController.getUserByEmail(inviteUser).isEmpty()){
            throw new Exception("El usuario que ha intentado invitar no existe");
        }else{

            Email emailDetails= new Email();
            emailDetails.setTo(inviteUser);
            emailDetails.setSubject("Invitación a Cuenta Compartida");
            Map<String, String> params = new HashMap<>();
            params.put("DueñoDeCuenta", currentUser.getEmail());
            params.put("invitado", inviteUser);
            emailSenderService.sendEmail(emailDetails, "InviteToShareAccount", params);
        }


    }

}