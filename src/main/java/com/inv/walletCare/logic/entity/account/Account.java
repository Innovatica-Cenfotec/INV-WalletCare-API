package com.inv.walletCare.logic.entity.account;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "account")
@Entity
public class Account {

    /**
     * The unique identifier for the account.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta")
    private Long id;

    /**
     * The name of the account, which is unique to the user.
     */
    @Column(name = "name", nullable = false, length = 100)
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios")
    private String name;

    /**
     * A brief description of the account for additional context.
     */
    @Column(name = "description", length = 200)
    @Length(groups = {OnCreate.class, OnUpdate.class }, max = 200, message = "La descripción debe tener menos de 200 caracteres")
    private String description;

    /**
     * The user who owns the account. This is a many-to-one relationship as a user can own multiple accounts.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * The type of the account, defined by the AccountType enum (PERSONAL, SHARED).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El tipo de cuenta es requerido")
    private AccountTypeEnum type;

    /**
     * The current balance of the account, stored as a BigDecimal for precision.
     */
    @Column(name = "balance", nullable = false, precision = 10, scale = 2)
    private BigDecimal balance;

    /**
     * The timestamp when the account was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * The timestamp of the last update made to the account's information.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * The timestamp when the account was marked as deleted, if applicable.
     */
    @Column(name = "deleted_at", nullable = true)
    private Date deletedAt;

    /**
     * A flag indicating whether the account is considered deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    /**
     * A flag indicating whether the account is the default account for the user.
     */
    @Column(name = "is_default", nullable = false)
    private boolean isDefault;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta") Long id) {
        this.id = id;
    }

    public @Size(min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres") @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios") String getName() {
        return name;
    }

    public void setName(@Size(min = 4, max = 100, message = "El nombre debe tener entre 4 y 100 caracteres") @Pattern(regexp = "^[a-zA-Z0-9 ]+$", message = "El nombre solo puede contener letras, números y espacios") String name) {
        this.name = name;
    }

    public @Length(max = 200, message = "La descripción debe tener menos de 200 caracteres") String getDescription() {
        return description;
    }

    public void setDescription(@Length(max = 200, message = "La descripción debe tener menos de 200 caracteres") String description) {
        this.description = description;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NotNull(message = "El tipo de cuenta es requerido") AccountTypeEnum getType() {
        return type;
    }

    public void setType(@NotNull(message = "El tipo de cuenta es requerido") AccountTypeEnum type) {
        this.type = type;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}