package com.inv.walletCare.logic.expenseCategory;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Date;

/**
 * Represents a category for expenses, owned by a user.
 */
@Entity
@Table(name = "expense_category")
public class ExpenseCategory {
    /**
     * Unique identifier for the expense category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar la categoría")
    private Long id;

    /**
     * Owner of the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Name of the expense category.
     */
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 50,
            message = "El nombre solo puede tener entre 4 y 50 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z ]+$",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    /**
     * Date and time when the expense category was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Date and time when the expense category was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Date and time when the expense category was deleted (can be null if the category is not deleted).
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Indicates whether the expense category is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar la categoría") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar la categoría") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 50,
            message = "El nombre solo puede tener entre 4 y 50 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z ]+$",
            message = "El nombre solo puede contener letras y espacios") String getName() {
        return name;
    }

    public void setName(@NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 50,
            message = "El nombre solo puede tener entre 4 y 50 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z ]+$",
            message = "El nombre solo puede contener letras y espacios") String name) {
        this.name = name;
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}
