package com.inv.walletCare.logic.entity.goal;

import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.saving.Saving;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

@Table(name = "goal")
@Entity
public class Goal {
    /**
     * ID for the goal.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar de la meta")
    private Long id;

    /**
     * Owner of the goal.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del usuario es requerido para asignar la meta")
    private User owner;

    /**
     * Account associated with the goal.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id" , referencedColumnName = "id", nullable = true)
    private Account account;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "saving_id", referencedColumnName = "id" , nullable = true)
    private Saving saving;

    /**
     * Name of the goal
     */
    @Column(name = "name", length = 100, nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "El nombre de la meta es requerido")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation;

    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El tipo de meta es requerido")
    private GoalTypeEnum type;

    @Column(name = "status", length = 50)
    private GoalStatusEnum status;

    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @Column(name = "initial_amount")
    private BigDecimal initialAmount;

    @Column(name = "target_date")
    private Date targetDate;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "deleted_at")
    private Date deletedAt;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar de la meta") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para actualizar de la meta") Long id) {
        this.id = id;
    }

    public @NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del usuario es requerido para asignar la meta") User getOwner() {
        return owner;
    }

    public void setOwner(@NegativeOrZero(groups = {OnCreate.class, OnUpdate.class},
            message = "El ID del usuario es requerido para asignar la meta") User owner) {
        this.owner = owner;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Saving getSaving() {
        return saving;
    }

    public void setSaving(Saving saving) {
        this.saving = saving;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El nombre de la meta es requerido") String getName() {
        return name;
    }

    public void setName(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El nombre de la meta es requerido") String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de meta es requerido") GoalTypeEnum getType() {
        return type;
    }

    public void setType(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de meta es requerido") GoalTypeEnum type) {
        this.type = type;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(BigDecimal initialAmount) {
        this.initialAmount = initialAmount;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public GoalStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GoalStatusEnum status) {
        this.status = status;
    }
}
