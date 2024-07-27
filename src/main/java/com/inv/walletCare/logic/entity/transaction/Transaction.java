package com.inv.walletCare.logic.entity.transaction;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.expenseAccount.ExpenseAccount;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocation;
import com.inv.walletCare.logic.entity.savingAllocation.SavingAllocation;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a transaction
 */
@Entity
@Table(name = "Transaction")
public class Transaction {
    /**
     * Unique identifier for the transaction.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la transacción")
    private Long id;

    /**
     * Owner of the transaction.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Account associated with the transaction.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido")
    private Account account;

    /**
     * Type of transaction: "income", "expense", "saving".
     */
    @Column(name = "type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionTypeEnum type;

    /**
     * Saving allocation associated with the transaction (can be null).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "saving_allocation_id", referencedColumnName = "id")
    private SavingAllocation savingAllocation;

    /**
     * Income allocation associated with the transaction (can be null).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "income_allocation_id", referencedColumnName = "id")
    private IncomeAllocation incomeAllocation;

    /**
     * Expense associated with the transaction (can be null).
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_account_id", referencedColumnName = "id")
    private ExpenseAccount expenseAccount;

    /**
     * Amount of the transaction.
     */
    @Column(name = "amount", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto es requerido")
    private BigDecimal amount;

    /**
     * Previous balance of the account before the transaction.
     */
    @Column(name = "previous_balance", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El saldo anterior es requerido")
    private BigDecimal previousBalance;

    /**
     * Is the Transaction description
     */
    @Column(name = "description", nullable = false)
    @NotNull(groups = {OnCreate.class}, message = "La transacción necesita una descripción")
    private String description;

    /**
     * Date and time when the transaction was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Date and time when the transaction was last updated.
     */
    @Column(name = "updated_at")
    private Date updatedAt;

    /**
     * Datetime when the transaction was deleted (can be null if the transaction is not deleted).
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Flag indicating whether the transaction is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public Transaction() {
    }

    public Transaction(Long id, User owner, Account account, TransactionTypeEnum type, SavingAllocation savingAllocation, IncomeAllocation incomeAllocation, ExpenseAccount expenseAccount, BigDecimal amount, BigDecimal previousBalance, String description, Date createdAt, Date updatedAt, Date deletedAt, Boolean isDeleted) {
        this.id = id;
        this.owner = owner;
        this.account = account;
        this.type = type;
        this.savingAllocation = savingAllocation;
        this.incomeAllocation = incomeAllocation;
        this.expenseAccount = expenseAccount;
        this.amount = amount;
        this.previousBalance = previousBalance;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.isDeleted = isDeleted;
    }

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la transacción") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar la transacción") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido") Account getAccount() {
        return account;
    }

    public void setAccount(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID de la cuenta es requerido") Account account) {
        this.account = account;
    }

    public TransactionTypeEnum getType() {
        return type;
    }

    public void setType(TransactionTypeEnum type) {
        this.type = type;
    }

    public SavingAllocation getSavingAllocation() {
        return savingAllocation;
    }

    public void setSavingAllocation(SavingAllocation savingAllocation) {
        this.savingAllocation = savingAllocation;
    }

    public IncomeAllocation getIncomeAllocation() {
        return incomeAllocation;
    }

    public void setIncomeAllocation(IncomeAllocation incomeAllocation) {
        this.incomeAllocation = incomeAllocation;
    }

    public ExpenseAccount getExpenseAccount() {
        return expenseAccount;
    }

    public void setExpenseAccount(ExpenseAccount expenseAccount) {
        this.expenseAccount = expenseAccount;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto es requerido") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto es requerido") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El saldo anterior es requerido") BigDecimal getPreviousBalance() {
        return previousBalance;
    }

    public void setPreviousBalance(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El saldo anterior es requerido") BigDecimal previousBalance) {
        this.previousBalance = previousBalance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    /**
     * Reverse the transaction type
     * @param type the transaction type reversed
     */
    public void rollbackTransactionType(TransactionTypeEnum type) {
        if (type.equals(TransactionTypeEnum.EXPENSE) || type.equals(TransactionTypeEnum.SAVING)) {
            this.setType(TransactionTypeEnum.INCOME);
        } else {
            this.setType(TransactionTypeEnum.EXPENSE);
        }
    }

    /**
     * Clones the current object
     * @return
     */
    public Transaction clone() {
        return new Transaction(
                null,
                null,
                this.getAccount(),
                null,
                this.getSavingAllocation(),
                this.getIncomeAllocation(),
                this.getExpenseAccount(),
                null,
                this.getPreviousBalance(),
                null,
                new Date(),
                null,
                null,
                false
        );
    }
}