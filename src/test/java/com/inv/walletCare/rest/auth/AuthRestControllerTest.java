package com.inv.walletCare.rest.auth;

import com.inv.walletCare.logic.entity.auth.AuthenticationService;
import com.inv.walletCare.logic.entity.auth.JwtService;
import com.inv.walletCare.logic.entity.rol.Role;
import com.inv.walletCare.logic.entity.rol.RoleEnum;
import com.inv.walletCare.logic.entity.rol.RoleRepository;
import com.inv.walletCare.logic.entity.user.LoginResponse;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRestControllerTest {

    @InjectMocks
    private AuthRestController authRestController;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authRestController, "userRepository", userRepository);
    }

    /**
     * Test that the authenticate method returns a valid token when the user is authenticated.
     */
    @Test
    void authenticate() {
        // Given
        User validUser = new User();
        validUser.setEmail("valid@walletcare.com");
        validUser.setPassword("validPassword");

        // When
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(validUser));
        when(authenticationService.authenticate(any(User.class))).thenReturn(validUser);
        when(jwtService.generateToken(any(User.class))).thenReturn("validToken");

        ResponseEntity<LoginResponse> response = authRestController.authenticate(validUser);

        // Then
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertNotNull(Objects.requireNonNull(response.getBody()).getToken());
        assertEquals("validToken", response.getBody().getToken());
    }

    /**
     * Test that the authenticate method throws an IllegalArgumentException when the user has an empty email.
     */
    @Test
    void authenticateWithEmptyEmailThrowsIllegalArgumentException() {
        // Given
        User userWithEmptyEmail = new User();
        userWithEmptyEmail.setEmail("");
        userWithEmptyEmail.setPassword("password123");

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authRestController.authenticate(userWithEmptyEmail));

        // Then
        assertEquals("Email is required", exception.getMessage());
    }

    /**
     * Test that the authenticate method throws an IllegalArgumentException when the user has an empty password.
     */
    @Test
    void authenticateWithEmptyPasswordThrowsIllegalArgumentException() {
        // Given
        User userWithEmptyPassword = new User();
        userWithEmptyPassword.setEmail("test@walletcare.com");
        userWithEmptyPassword.setPassword("");

        // When
        Exception exception = assertThrows(IllegalArgumentException.class, () -> authRestController.authenticate(userWithEmptyPassword));

        // Then
        assertEquals("Password is required", exception.getMessage());
    }
}