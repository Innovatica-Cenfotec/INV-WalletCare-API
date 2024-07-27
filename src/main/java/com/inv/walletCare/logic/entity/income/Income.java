package com.inv.walletCare.logic.entity.income;

import com.inv.walletCare.logic.entity.AmountTypeEnum;
import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.incomeAllocation.IncomeAllocation;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Represents an income.
 */
@Table(name = "income")
@Entity
public class Income {
    /**
     * Unique identifier for the income.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un ingreso")
    private Long id;

    /**
     * Owner of the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Name of the income
     */
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 50,
            message = "El nombre solo puede tener entre 4 y 50 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z ]+$",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    /**
     * Description of the income.
     */
    @Column(name = "description", length = 255)
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "La descripción debe tener menos de 255 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z0-9 ]+$",
            message = "La descripción solo puede contener letras, números y espacios")
    private String description;

    /**
     * Indicates whether the income is a template.
     */
    @Column(name = "is_template", nullable = false)
    @NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla")
    private boolean isTemplate;

    /**
     * Type of income: "unique" or "recurring".
     */
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido")
    private IncomeExpenceType type;

    /**
     * Amount of the income.
     */
    @Column(name = "amount", nullable = false)
    @Min(groups = {OnCreate.class, OnUpdate.class }, value = 0, message = "El monto del ingreso debe ser mayor a 0")
    private BigDecimal amount;

    @Column(name = "amount_type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El tipo de monto es requerido")
    private AmountTypeEnum amountType;

    /**
     * Frequency of the recurring income: "monthly", "annual", "biweekly", "other" (applies only to recurring incomes).
     */
    @Column(name = "frequency", length = 50)
    @Enumerated(EnumType.STRING)
    private FrequencyTypeEnum frequency;

    /**
     * Scheduled day of the month for recurring incomes (applies only to frequency "other", between 1 and 31).
     */
    @Column(name = "scheduled_day")
    private short scheduledDay;

    /**
     * Indicates whether the income is relevant for tax declaration.
     */
    @Column(name = "is_tax_related", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar si el ingreso es relevante para la declaración de impuestos")
    private boolean isTaxRelated;

    /**
     * Tax associated with the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tax_id", referencedColumnName = "id")
    private Tax tax;

    /**
     * Datetime when the income was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Datetime when the income was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Datetime when the income was deleted.
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * List of income allocations.
     */
    @OneToMany(mappedBy = "income", fetch = FetchType.EAGER)
    private List<IncomeAllocation> incomeAllocations;

    /**
     * Flag to indicate if the income is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un ingreso") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un ingreso") Long id) {
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

    public @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "La descripción debe tener menos de 255 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z0-9 ]+$",
            message = "La descripción solo puede contener letras, números y espacios") String getDescription() {
        return description;
    }

    public void setDescription(@Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "La descripción debe tener menos de 255 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "^[a-zA-Z0-9 ]+$",
            message = "La descripción solo puede contener letras, números y espacios") String description) {
        this.description = description;
    }

    @NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla")
    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(@NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla") boolean template) {
        isTemplate = template;
    }

    public @NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido") IncomeExpenceType getType() {
        return type;
    }

    public void setType(@NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido") IncomeExpenceType type) {
        this.type = type;
    }

    public @Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del ingreso debe ser mayor a 0") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del ingreso debe ser mayor a 0") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de monto es requerido") AmountTypeEnum getAmountType() {
        return amountType;
    }

    public void setAmountType(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de monto es requerido") AmountTypeEnum amountType) {
        this.amountType = amountType;
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

    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el ingreso es relevante para la declaración de impuestos")
    public boolean isTaxRelated() {
        return isTaxRelated;
    }

    public void setTaxRelated(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el ingreso es relevante para la declaración de impuestos") boolean taxRelated) {
        isTaxRelated = taxRelated;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
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

    public List<IncomeAllocation> getIncomeAllocations() {
        return incomeAllocations;
    }

    public void setIncomeAllocations(List<IncomeAllocation> incomeAllocations) {
        this.incomeAllocations = incomeAllocations;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}