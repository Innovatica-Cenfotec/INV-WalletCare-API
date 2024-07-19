package com.inv.walletCare.rest.user;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link UserRestController}.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserRestControllerTest {

    @InjectMocks
    private UserRestController userRestController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    /**
     * Set up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userRestController, "userRepository", userRepository);
        ReflectionTestUtils.setField(userRestController, "passwordEncoder", passwordEncoder);
    }

    /**
     * Dynamically provides properties for the test context.
     *
     * @param registry the dynamic property registry
     */
    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("jasypt.encryptor.password", () -> "innova");
        registry.add("spring.datasource.url", () -> "jdbc:mariadb://localhost:3305/walletcare");
        registry.add("spring.datasource.username", () -> "guillermo");
        registry.add("spring.datasource.password", () -> "guillermo2024");
    }

    /**
     * Test for retrieving all users.
     */
    @Test
    void getAllUsers() {
        // Given
        User user1 = new User();
        user1.setName("User1");
        User user2 = new User();
        user2.setName("User2");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // When
        List<User> users = userRestController.getAllUsers();

        // Then
        assertEquals(2, users.size());
        assertEquals("User1", users.get(0).getName());
        assertEquals("User2", users.get(1).getName());
    }

    /**
     * Test for adding a new user.
     */
    @Test
    void addUser() {
        // Given
        User newUser = new User();
        newUser.setPassword("password123");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User savedUser = userRestController.addUser(newUser);

        // Then
        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    /**
     * Test for retrieving a user by ID.
     */
    @Test
    void getUserById() {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        // When
        User foundUser = userRestController.getUserById(1L);

        // Then
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
    }

    /**
     * Test for updating an existing user.
     */
    @Test
    void updateUser() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("OldName");

        User updatedUser = new User();
        updatedUser.setName("NewName");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userRestController.updateUser(1L, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("NewName", result.getName());
    }

    /**
     * Test for deleting a user by ID.
     */
    @Test
    void deleteUser() {
        // Given
        doNothing().when(userRepository).deleteById(anyLong());

        // When
        userRestController.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).deleteById(1L);
    }

    /**
     * Test for updating the authenticated user's information.
     */
    @Test
    void updateAuthenticatedUser() {
        // Given
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setName("OldName");

        User updatedUser = new User();
        updatedUser.setName("NewName");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        // When
        User result = userRestController.updateAuthenticatedUser(1L, updatedUser);

        // Then
        assertNotNull(result);
        assertEquals("NewName", result.getName());
    }
}
