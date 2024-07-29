package com.inv.walletCare.logic.entity.expenseRecurrence;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.expense.Expense;
import com.inv.walletCare.logic.entity.user.User;
import jakarta.persistence.*;

import java.util.Date;

/**
 * Represents an expense recurrence entity.
 */
@Entity
@Table(name = "expense_recurrence")
public class ExpenseRecurrence {
    /**
     * The unique identifier for the expense recurrence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The owner of the expense recurrence.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * The expense associated with the recurrence.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_id", referencedColumnName = "id", nullable = false)
    private Expense expense;

    /**
     * The account associated with the expense recurrence.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    /**
     * The date and time when the expense recurrence was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * The date and time when the expense recurrence was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Flag that indicates if the expense recurrence has been deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}