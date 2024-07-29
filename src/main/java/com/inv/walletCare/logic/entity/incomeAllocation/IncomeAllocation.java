package com.inv.walletCare.logic.entity.incomeAllocation;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents the allocation of income to specific accounts.
 */
@Entity
@Table(name = "income_allocation")
public class IncomeAllocation {
    /**
     * Unique identifier for the income allocation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asignación de ingreso")
    private Long id;

    /**
     * Account associated with the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido para la asignación")
    private Account account;

    /**
     * Income being allocated.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "income_id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ingreso es requerido para crear o actualizar la asignación")
    private Income income;

    /**
     * Owner of the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Percentage of the income allocated to the account (between 0 and 1).
     */
    @Column(name = "percentage", nullable = false)
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 0, max = 1,
            message = "El porcentaje de la asignación debe estar entre 0 y 1")
    private BigDecimal percentage;

    /**
     * Date and time when the allocation record was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Date and time when the allocation record was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Date and time when the allocation record was deleted (can be null if the record is not deleted).
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Indicates whether the allocation record is deleted (true) or not (false).
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asignación de ingreso") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asignación de ingreso") Long id) {
        this.id = id;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido la asignación") Account getAccount() {
        return account;
    }

    public void setAccount(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido la asignación") Account account) {
        this.account = account;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ingreso es requerido para crear o actualizar la asignación") Income getIncome() {
        return income;
    }

    public void setIncome(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del ingreso es requerido para crear o actualizar la asignación") Income income) {
        this.income = income;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, max = 1,
            message = "El porcentaje de la asignación debe estar entre 0 y 1") BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(@Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, max = 1,
            message = "El porcentaje de la asignación debe estar entre 0 y 1") BigDecimal percentage) {
        this.percentage = percentage;
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