package com.inv.walletCare.rest.user;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * REST controller for managing users in the WalletCare system.
 */
@RestController
@RequestMapping("/users")
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Retrieves all users.
     *
     * @return a list of all users.
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Adds a new user.
     *
     * @param user the user to be added.
     * @return the added user.
     */
    @PostMapping
    public User addUser(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(true);
        return userRepository.save(user);
    }

    /**
     * Retrieves a user by ID.
     *
     * @param id the ID of the user to retrieve.
     * @return the user with the specified ID.
     */
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    /**
     * Retrieves users whose names contain the specified character.
     *
     * @param name the character to filter users by.
     * @return a list of users whose names contain the specified character.
     */
    @GetMapping("/filterByName/{name}")
    public List<User> getUserByName(@PathVariable String name) {
        return userRepository.findUsersWithCharacterInName(name);
    }

    /**
     * Updates an existing user.
     *
     * @param id   the ID of the user to update.
     * @param user the new user data.
     * @return the updated user.
     */
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setLastname(user.getLastname());
                    existingUser.setEmail(user.getEmail());
                    existingUser.setEnabled(user.isEnabled());
                    return userRepository.save(existingUser);
                })
                .orElseGet(() -> {
                    user.setId(id);
                    return userRepository.save(user);
                });
    }

    /**
     * Deletes a user by ID.
     *
     * @param id the ID of the user to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Retrieves the authenticated user.
     *
     * @return the authenticated user.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public User authenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    /**
     * Updates the authenticated user's information.
     *
     * @param id   the ID of the user to update.
     * @param user the new user data.
     * @return the updated user.
     */
    @PutMapping("/me/{id}")
    @PreAuthorize("isAuthenticated()")
    public User updateAuthenticatedUser(@PathVariable Long id, @RequestBody User user) {
        return userRepository.findById(id)
                .map(existingUser -> {
                    existingUser.setName(user.getName());
                    existingUser.setLastname(user.getLastname());
                    existingUser.setNickname(user.getNickname());
                    existingUser.setEmail(user.getEmail());
                    return userRepository.save(existingUser);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/new-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public int[] getNewUsersThisYear() {

        int[] countUsers = new int[12];

        for (var user : userRepository.findAll()) {
            if (user.getCreatedAt().getYear() == new Date().getYear()) {
                for (int i = 0; i <= 11; i++) {
                    //Month validations
                    if (user.getCreatedAt().getMonth() == i) {
                        countUsers[i] = countUsers[i] + 1;
                    }

                }
            }
        }

        return countUsers;
    }
}
