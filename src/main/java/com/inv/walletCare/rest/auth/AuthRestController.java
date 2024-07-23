package com.inv.walletCare.rest.auth;

import com.inv.walletCare.logic.entity.Response;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.account.AccountRepository;
import com.inv.walletCare.logic.entity.account.AccountTypeEnum;
import com.inv.walletCare.logic.entity.rol.Role;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.rol.RoleRepository;
import com.inv.walletCare.logic.entity.user.RegisterUserRequest;
import com.inv.walletCare.logic.entity.user.UserRepository;
import com.inv.walletCare.logic.entity.auth.AuthenticationService;
import com.inv.walletCare.logic.entity.auth.JwtService;
import com.inv.walletCare.logic.entity.user.LoginResponse;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.exceptions.FieldValidationException;
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
     * Registers a new user with the provided details.
     *
     * @param request the request containing the user details and account name
     * @return ResponseEntity containing the saved user entity
     */
    @PostMapping("/signup")
    @Transactional
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest request) {
        // Validate the user's input
        if (request.getUser().getEmail() == null || request.getUser().getEmail().isEmpty()) {
            throw new FieldValidationException("email", "El correo electrónico es requerido");
        }
        if (request.getUser().getPassword() == null || request.getUser().getPassword().isEmpty()) {
            throw new FieldValidationException("password", "La contraseña es requerida");
        }

        Optional<User> existingUser = userRepository.findByEmail(request.getUser().getEmail());
        if (existingUser.isPresent()) {
            throw new FieldValidationException("email", "Ya existe una cuenta con este correo.");
        }

        existingUser = userRepository.findByNickname(request.getUser().getNickname());
        if (existingUser.isPresent()) {
            throw new FieldValidationException("nickname", "Ya existe una cuenta con este alias.");
        }

        existingUser = userRepository.findByIdentificationNumber(request.getUser().getIdentificationNumber());
        if (existingUser.isPresent()) {
            throw new FieldValidationException("identificationNumber", "Ya existe una cuenta con este número de identificación.");
        }

        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if (optionalRole.isEmpty()) {
            throw new IllegalStateException("Rol por defecto USER no encontrado.");
        }

        // Create a new user
        User user = new User();
        user.setName(request.getUser().getName());
        user.setLastname(request.getUser().getLastname());
        user.setEmail(request.getUser().getEmail());
        user.setNickname(request.getUser().getNickname());
        user.setIdentificationNumber(request.getUser().getIdentificationNumber());
        user.setPassword(passwordEncoder.encode(request.getUser().getPassword()));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setRole(optionalRole.get());
        userRepository.save(user);

        // Create a new account for the user
        Account newAccount = new Account();
        newAccount.setName(request.getAccountName());
        newAccount.setDescription(request.getAccountDescription());
        newAccount.setOwner(user);
        newAccount.setType(AccountTypeEnum.PERSONAL); // Set your default account type here
        newAccount.setBalance(BigDecimal.ZERO);
        newAccount.setCreatedAt(new Date());
        newAccount.setUpdatedAt(new Date());
        newAccount.setDeleted(false);
        newAccount.setDefault(true);
        accountRepository.save(newAccount);

        return ResponseEntity.ok(new Response("Usuario registrado exitosamente"));
    }
}