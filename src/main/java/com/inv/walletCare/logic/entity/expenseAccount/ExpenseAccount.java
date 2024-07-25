package com.inv.walletCare.logic.entity.expenseAccount;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;

import java.util.Date;

/**
 * Represents an association between an expense and an account.
 */
@Entity
@Table(name = "expense_account")
public class ExpenseAccount {
    /**
     * Unique identifier for the association.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asociación de gasto y cuenta")
    private Long id;

    /**
     * Expense associated with the account.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID del gasto es requerido para actualizar la asociación")
    private Expense expense;

    /**
     * Account associated with the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID de la cuenta es requerido para actualizar la asociación")
    private Account account;

    /**
     * Datetime when the association was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asociación de gasto y cuenta") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la asociación de gasto y cuenta") Long id) {
        this.id = id;
    }

    public @NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID del gasto es requerido para actualizar la asociación") Expense getExpense() {
        return expense;
    }

    public void setExpense(@NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID del gasto es requerido para actualizar la asociación") Expense expense) {
        this.expense = expense;
    }

    public @NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID de la cuenta es requerido para actualizar la asociación") Account getAccount() {
        return account;
    }

    public void setAccount(@NegativeOrZero(groups = {OnUpdate.class, OnCreate.class},
            message = "El ID de la cuenta es requerido para actualizar la asociación") Account account) {
        this.account = account;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}