package com.inv.walletCare.logic.entity.recurrence;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.income.Income;
import com.inv.walletCare.logic.entity.user.User;
import jakarta.persistence.*;

import java.util.Date;

/**
 * Represents a recurrence entity.
 */
@Entity
@Table(name = "recurrence")
public class Recurrence {

    /**
     * The unique identifier for the recurrence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The owner of the recurrence.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * The account associated with the recurrence.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    /**
     * The expense associated with the recurrence.
     * This field is optional.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_id", referencedColumnName = "id")
    private Expense expense;

    /**
     * The income associated with the recurrence.
     * This field is optional.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "income_id", referencedColumnName = "id")
    private Income income;

    /**
     * The date and time when the recurrence was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Indicates whether the recurrence is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Expense getExpense() {
        return expense;
    }

    public void setExpense(Expense expense) {
        this.expense = expense;
    }

    public Income getIncome() {
        return income;
    }

    public void setIncome(Income income) {
        this.income = income;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }


    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}