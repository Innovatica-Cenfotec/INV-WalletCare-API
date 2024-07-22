package com.inv.walletCare.logic.entity.account;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "account_user")
@Entity
public class AccountUser {

    /**
     * The unique identifier for the account user relationship.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta")
    private Long id;

    /**
     * The account associated with the user.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id", nullable = false)
    private Account account;

    /**
     * The user associated with the account.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    /**
     * Indicates whether the user is actively associated with the account.
     */
    @Column(nullable = false)
    private Boolean isActive;

    /**
     * The ID of the last transaction performed by the user on the account.
     */
    @Column(name = "last_transaction_id")
    private Long lastTransactionId;

    /**
     * The balance of the account after the last transaction by the user.
     */
    @Column(name = "last_transaction_balance", precision = 10, scale = 2)
    private BigDecimal lastTransactionBalance;

    /**
     * The timestamp when the user joined the account.
     */
    @Column(name = "joined_at", nullable = false)
    private Date joinedAt;

    /**
     * The timestamp when the user left the account, if applicable.
     */
    @Column(name = "left_at")
    private Date leftAt;

    /**
     * The status of the invitation to join the account, if applicable.
     */
    @Column(name = "invitation_status")
    private Integer invitationStatus;

    /**
     * Flag indicating whether the relationship is considered deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Integer getInvitationStatus() {
        return invitationStatus;
    }

    public void setInvitationStatus(Integer invitationStatus) {
        this.invitationStatus = invitationStatus;
    }

    public Date getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(Date leftAt) {
        this.leftAt = leftAt;
    }

    public Date getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(Date joinedAt) {
        this.joinedAt = joinedAt;
    }

    public BigDecimal getLastTransactionBalance() {
        return lastTransactionBalance;
    }

    public void setLastTransactionBalance(BigDecimal lastTransactionBalance) {
        this.lastTransactionBalance = lastTransactionBalance;
    }

    public Long getLastTransactionId() {
        return lastTransactionId;
    }

    public void setLastTransactionId(Long lastTransactionId) {
        this.lastTransactionId = lastTransactionId;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar una cuenta") Long id) {
        this.id = id;
    }
}