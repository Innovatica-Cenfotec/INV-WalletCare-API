package com.inv.walletCare.logic.entity.user;

import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Length;

/**
 * A request object for registering a new user.
 */
public class RegisterUserRequest {
    /**
     * The user details to be registered.
     */
    private User user;

    /**
     * The name of the account, which is unique to the user.
     */
    @Column(name = "name", nullable = false, length = 100)
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String accountName;

    /**
     * A brief description of the account for additional context.
     */
    @Column(name = "description", length = 200)
    @Length(groups = {OnCreate.class, OnUpdate.class }, max = 200, message = "La descripción debe tener menos de 200 caracteres")
    private String accountDescription;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public @Length(groups = {OnCreate.class, OnUpdate.class}, max = 200, message = "La descripción debe tener menos de 200 caracteres") String getAccountDescription() {
        return accountDescription;
    }

    public void setAccountDescription(@Length(groups = {OnCreate.class, OnUpdate.class}, max = 200, message = "La descripción debe tener menos de 200 caracteres") String accountDescription) {
        this.accountDescription = accountDescription;
    }

    public @Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios") String getAccountName() {
        return accountName;
    }

    public void setAccountName(@Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios") String accountName) {
        this.accountName = accountName;
    }
}