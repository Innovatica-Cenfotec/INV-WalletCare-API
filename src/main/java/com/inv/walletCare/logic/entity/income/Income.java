package com.inv.walletCare.logic.entity.income;


import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxPurposeTypeEnum;
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
     * Indicates whether the income is a template.
     */
    @Column(name = "is_template", nullable = false)
    @NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla")
    private Boolean isTemplate;

    /**
     * Type of income: "unique" or "recurring".
     */
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido")
    private IncomeTypeEnum type;

    /**
     * Indicates whether the income is net or gross.
     */
    @Column(name = "is_net", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "Debe indicar si el ingreso es neto o bruto")
    private Boolean isNet;

    /**
     * Amount of the income.
     */
    @Column(name = "amount", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El monto del ingreso es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 0, message = "El monto del ingreso debe ser mayor a 0")
    private BigDecimal amount;

    /**
     * Frequency of the recurring income: "monthly", "annual", "biweekly", "other" (applies only to recurring incomes).
     */
    @Column(name = "frequency", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "La frecuencia del ingreso es requerida")
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
    private Boolean isTaxRelated;

    /**
     * Type of income for tax purposes: "Gross" or "Net"
     */
    @Column(name = "tax_type", length = 50)
    @Enumerated(EnumType.STRING)
    private TaxPurposeTypeEnum taxType;

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
     * Flag to indicate if the income is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Tax getTax() {
        return tax;
    }

    public void setTax(Tax tax) {
        this.tax = tax;
    }

    public TaxPurposeTypeEnum getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxPurposeTypeEnum taxType) {
        this.taxType = taxType;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el ingreso es relevante para la declaración de impuestos") Boolean getTaxRelated() {
        return isTaxRelated;
    }

    public void setTaxRelated(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el ingreso es relevante para la declaración de impuestos") Boolean taxRelated) {
        isTaxRelated = taxRelated;
    }

    public short getScheduledDay() {
        return scheduledDay;
    }

    public void setScheduledDay(short scheduledDay) {
        this.scheduledDay = scheduledDay;
    }

    public @NotNull(groups = OnCreate.class, message = "La frecuencia del ingreso es requerida") FrequencyTypeEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(@NotNull(groups = OnCreate.class, message = "La frecuencia del ingreso es requerida") FrequencyTypeEnum frequency) {
        this.frequency = frequency;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto del ingreso es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, message = "El monto del ingreso debe ser mayor a 0") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto del ingreso es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, message = "El monto del ingreso debe ser mayor a 0") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Debe indicar si el ingreso es neto o bruto") Boolean getNet() {
        return isNet;
    }

    public void setNet(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "Debe indicar si el ingreso es neto o bruto") Boolean net) {
        isNet = net;
    }

    public @NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido") IncomeTypeEnum getType() {
        return type;
    }

    public void setType(@NotNull(groups = OnCreate.class, message = "El tipo de ingreso es requerido") IncomeTypeEnum type) {
        this.type = type;
    }

    public @NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla") Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(@NotNull(groups = OnCreate.class, message = "Debe indicar si el ingreso es una plantilla") Boolean template) {
        isTemplate = template;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un ingreso") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un ingreso") Long id) {
        this.id = id;
    }
}