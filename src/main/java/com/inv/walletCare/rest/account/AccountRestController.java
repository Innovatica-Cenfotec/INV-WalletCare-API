package com.inv.walletCare.rest.account;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
import com.inv.walletCare.logic.validation.OnCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Controller for account-related operations.
 * Handles HTTP requests for account management within the application.
 */
@RestController
@RequestMapping("/accounts")
public class AccountRestController {

    @Autowired
    private AccountRepository accountRepository;

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
}