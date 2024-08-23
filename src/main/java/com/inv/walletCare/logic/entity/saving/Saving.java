package com.inv.walletCare.logic.entity.saving;

import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents a saving goal or account in the system, owned by a user.
 */
@Table(name = "saving")
@Entity
public class Saving {
    /**
     * Unique identifier for the saving.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnCreate.class, message = "El ID es requerido para actualizar un ahorro")
    private Long id;

    /**
     * Owner of the saving.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Name of the saving, e.g., "Vacation", "New Car", "Emergency Fund".
     */
    @Column(name = "name", length = 100, nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "El nombre del ahorro es requerido")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras, números y espacios")
    private String name;

    /**
     * Description of the tax.
     */
    @Column(name = "description", length = 255)
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "La descripción debe tener menos de 255 caracteres")
    private String description;

    /**
     * DateTIme when the saving was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name="target_date", nullable = true)
    private Date targetDate;

    /**
     * DateTIme when the saving was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * DateTime when the saving was deleted.
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Flag indicating whether the saving has been deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    @Column(name = "frequency", length = 50)
    @Enumerated(EnumType.STRING)
    private FrequencyTypeEnum frequency;

    @Column(name = "scheduled_day")
    private short scheduledDay;

    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "El tipo de ahorro es requerido")
    private SavingTypeEnum type;

    @Transient
    private boolean addTransaction;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "amount", nullable = false)
    @Min(groups = {OnCreate.class, OnUpdate.class }, value = 0, message = "El monto del ahorro debe ser mayor a 0")
    private BigDecimal amount;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    public @NegativeOrZero(groups = OnCreate.class, message = "El ID es requerido para actualizar un ahorro") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnCreate.class, message = "El ID es requerido para actualizar un ahorro") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El nombre del ahorro es requerido") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras, números y espacios") String getName() {
        return name;
    }

    public void setName(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El nombre del ahorro es requerido") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras, números y espacios") String name) {
        this.name = name;
    }

    public @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "La descripción debe tener menos de 255 caracteres") String getDescription() {
        return description;
    }

    public void setDescription(@Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "La descripción debe tener menos de 255 caracteres") String description) {
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

    public FrequencyTypeEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(FrequencyTypeEnum frequency) {
        this.frequency = frequency;
    }

    public short getScheduledDay() {
        return scheduledDay;
    }

    public void setScheduledDay(short scheduledDay) {
        this.scheduledDay = scheduledDay;
    }

    public @NotNull(groups = OnCreate.class, message = "El tipo de ahorro es requerido") SavingTypeEnum getType() {
        return type;
    }

    public void setType(@NotNull(groups = OnCreate.class, message = "El tipo de ahorro es requerido") SavingTypeEnum type) {
        this.type = type;
    }

    public Date getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Date targetDate) {
        this.targetDate = targetDate;
    }

    public boolean isAddTransaction() {
        return addTransaction;
    }

    public void setAddTransaction(boolean addTransaction) {
        this.addTransaction = addTransaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public @Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del gasto debe ser mayor a 0") BigDecimal getAmount() {
        return amount;
    }
  
    public void setAmount(@Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del ahorro debe ser mayor a 0") BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
