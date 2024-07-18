package com.inv.walletCare.rest.auth;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.account.AccountTypeEnum;
import com.inv.walletCare.logic.entity.rol.Role;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.rol.RoleRepository;
import com.inv.walletCare.logic.entity.user.UserRepository;
import com.inv.walletCare.logic.entity.auth.AuthenticationService;
import com.inv.walletCare.logic.entity.auth.JwtService;
import com.inv.walletCare.logic.entity.user.LoginResponse;
import com.inv.walletCare.logic.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * Controller for authentication-related actions including login and signup.
 */
@RequestMapping("/auth")
@RestController
public class AuthRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    /**
     * Constructs an AuthRestController with specified JWT and authentication services.
     *
     * @param jwtService the service to generate and manage JWT tokens
     * @param authenticationService the service to authenticate users
     */
    public AuthRestController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    /**
     * Authenticates a user based on email and password, returning a login response with a JWT token.
     *
     * @param user the user attempting to log in
     * @return ResponseEntity containing the login response with token and expiration time
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody User user) {
        // Validate the user's input
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }

        User authenticatedUser = authenticationService.authenticate(user);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtService.generateToken(authenticatedUser));
        loginResponse.setExpiresIn(jwtService.getExpirationTime());

        Optional<User> foundedUser = userRepository.findByEmail(user.getEmail());
        foundedUser.ifPresent(loginResponse::setAuthUser);

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * Registers a new user with email and password, assigning them a default role.
     *
     * @param user the user to register
     * @param accountName the name of account the user wants to create
     * @param accountDescription the description of account the user wants to create
     * @return ResponseEntity containing the saved user
     */
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody User user,
                                          @RequestParam String accountName,
                                          @RequestParam String accountDescription) {
        // Validate the user's input
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con este correo.");
        }

        existingUser = userRepository.findByNickname(user.getNickname());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con este alias.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            throw new IllegalStateException("Rol por defecto USER no encontrado.");
        }

        user.setRole(optionalRole.get());
        User savedUser = userRepository.save(user);

        // Create a new account for the user
        Account newAccount = new Account();
        newAccount.setName(accountName);
        newAccount.setDescription(accountDescription);
        newAccount.setOwner(savedUser);
        newAccount.setType(AccountTypeEnum.PERSONAL); // Set your default account type here
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCreatedAt(new Date());
        newAccount.setUpdatedAt(new Date());
        newAccount.setDeleted(false);

        accountRepository.save(newAccount);

        return ResponseEntity.ok(savedUser);
    }
}