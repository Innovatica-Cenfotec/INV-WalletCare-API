package com.inv.walletCare.logic.entity.savingAllocation;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.saving.Saving;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents the allocation of surplus funds to saving.
 */
@Entity
@Table(name = "Saving_Allocation")

public class SavingAllocation {
    /**
     * Unique identifier for the saving allocation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar el ahorro")
    private Long id;

    /**
     * Owner of the saving allocation.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Saving allocation.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "saving_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ahorro es requerido para realizar la asignación")
    private Saving saving;

    /**
     * Account from which the surplus is taken.
     */
    @ManyToOne
    @JoinColumn(name = "account_id",  referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido para realizar la asignación")
    private Account account;

    /**
     * Percentage allocated to the saving (between 0 and 1).
     */
    @Column(name = "percentage", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El monto es requerido para realizar la asignación")
    @Size(min = 1, groups = {OnCreate.class, OnUpdate.class},
            message = "El monto debe tener un valor mayor o igual a 1")
    private BigDecimal amount;

    /**
     * Date and time when the saving allocation was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Date and time when the saving allocation was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Date and time when the saving allocation was deleted (can be null if the allocation is not deleted).
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Flag indicating whether the saving allocation is deleted (true) or not (false).
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar el ahorro") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar el ahorro") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ahorro es requerido para realizar la asignación") Saving getSaving() {
        return saving;
    }

    public void setSaving(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ahorro es requerido para realizar la asignación") Saving saving) {
        this.saving = saving;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido para realizar la asignación") Account getAccount() {
        return account;
    }

    public void setAccount(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido para realizar la asignación") Account account) {
        this.account = account;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El monto es requerido para realizar la asignación") @Size(min = 1, groups = {OnCreate.class, OnUpdate.class},
            message = "El monto debe tener un valor mayor o igual a 1") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El monto es requerido para realizar la asignación") @Size(min = 1, groups = {OnCreate.class, OnUpdate.class},
            message = "El monto debe tener un valor mayor o igual a 1") BigDecimal percentage) {
        this.amount = percentage;
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