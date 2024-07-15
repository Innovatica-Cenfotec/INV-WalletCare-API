package com.inv.walletCare.rest.account;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.account.AccountTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Test class for AccountRestController.
 * This class contains unit tests for the account creation functionality.
 */
@ExtendWith(MockitoExtension.class)
class AccountRestControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountRestController accountRestController;

    private User currentUser;

    /**
     * Sets up the testing environment before each test.
     * This includes initializing a mock user and setting up the security context.
     */
    @BeforeEach
    void setUp() {
        currentUser = new User();
        currentUser.setId(1L);

        // Mock the security context to return the current user
        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        // Mock the authentication to return the current user
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getPrincipal()).thenReturn(currentUser);
    }

    /**
     * Tests the addition of a personal account with a unique name.
     * Ensures that the account is successfully created and all fields are correctly set.
     */
    @Test
    void addPersonalAccount() {
        // Given
        Account account = new Account();
        account.setName("Test Account");
        account.setDescription("Test Description");
        account.setType(AccountTypeEnum.PERSONAL);

        // When
        when(accountRepository.findByNameAndOwnerId(account.getName(), currentUser.getId())).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account result = accountRestController.AddAccount(account);

        // Then
        assertNotNull(result);
        assertEquals("Test Account", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(AccountTypeEnum.PERSONAL, result.getType());
        assertEquals(BigDecimal.ZERO, result.getBalance());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertFalse(result.getDeleted());
    }

    /**
     * Tests the addition of a personal account with an existing name.
     * Verifies that an IllegalArgumentException is thrown with the correct message.
     */
    @Test
    void addPersonalAccountWithExistingName() {
        // Given
        Account account = new Account();
        account.setName("Test Account");
        account.setDescription("Test Description");
        account.setType(AccountTypeEnum.PERSONAL);

        // When
        when(accountRepository.findByNameAndOwnerId(account.getName(), currentUser.getId())).thenReturn(Optional.of(new Account()));

        // Then
        Exception exception = assertThrows(IllegalArgumentException.class, () -> accountRestController.AddAccount(account));
        assertTrue(exception.getMessage().contains("El ya está en uso, por favor elija otro"));
    }
}