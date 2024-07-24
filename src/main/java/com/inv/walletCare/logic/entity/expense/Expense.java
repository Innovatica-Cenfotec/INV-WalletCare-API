package com.inv.walletCare.logic.entity.expense;

import com.inv.walletCare.logic.entity.income.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.income.IncomeTypeEnum;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.tax.TaxPurposeTypeEnum;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.expenseCategory.ExpenseCategory;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.NegativeOrZero;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Represents an expense.
 */
@Entity
@Table(name = "expense")
public class Expense {
    /**
     * Unique identifier for the expense.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un gasto")
    private Long id;

    /**
     * Owner of the income.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Category associated with the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_category_id", referencedColumnName = "id")
    private ExpenseCategory expenseCategory;

    /**
     * Name of the tax
     */
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 50,
            message = "El nombre solo puede tener entre 4 y 50 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z ]+$",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    /**
     * Description of the tax.
     */
    @Column(name = "description", length = 255)
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "La descripción debe tener menos de 255 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "^[a-zA-Z0-9 ]+$",
            message = "La descripción solo puede contener letras, números y espacios")
    private String description;

    /**
     * Amount of the expense.
     */
    @Column(name = "amount", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El monto del gasto es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 0, message = "El monto del gasto debe ser mayor a 0")
    private BigDecimal amount;

    /**
     * Indicates whether the expense is a template.
     */
    @Column(name = "is_template", nullable = false)
    @NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla")
    private Boolean isTemplate;

    /**
     * Type of expense: "unique" or "recurring".
     */
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido")
    private ExpenseTypeEnum type;

    /**
     * Frequency of the recurring: "monthly", "annual", "biweekly", "other" (applies only to recurring incomes).
     */
    @Column(name = "frequency", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "La frecuencia del gasto es requerida")
    private FrequencyTypeEnum frequency;

    /**
     * Scheduled day of the month for recurring expenses (optional if type is "recurring" and frequency is "other", between 1 and 31).
     */
    @Column(name = "scheduled_day")
    private short scheduledDay;

    /**
     * Amount of the expense.
     */
    @Column(name = "is_tax_related", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar si el gasto es relevante para la declaración de impuestos")
    private Boolean isTaxRelated;

    /**
     * Type of expense for tax purposes: "Gross" or "Net"
     */
    @Column(name = "tax_type", length = 50)
    @Enumerated(EnumType.STRING)
    private TaxPurposeTypeEnum taxType;

    /**
     * Tax associated with the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tax_id", referencedColumnName = "id")
    private Tax tax;

    /**
     * Datetime when the expense was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Datetime when the expense was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Datetime when the expense was deleted.
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Flag indicating whether the expense is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un gasto") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un gasto") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
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

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto del gasto es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, message = "El monto del gasto debe ser mayor a 0") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El monto del gasto es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, message = "El monto del gasto debe ser mayor a 0") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla") Boolean getTemplate() {
        return isTemplate;
    }

    public void setTemplate(@NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla") Boolean template) {
        isTemplate = template;
    }

    public @NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido") ExpenseTypeEnum getType() {
        return type;
    }

    public void setType(@NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido") ExpenseTypeEnum type) {
        this.type = type;
    }

    public @NotNull(groups = OnCreate.class, message = "La frecuencia del gasto es requerida") FrequencyTypeEnum getFrequency() {
        return frequency;
    }

    public void setFrequency(@NotNull(groups = OnCreate.class, message = "La frecuencia del gasto es requerida") FrequencyTypeEnum frequency) {
        this.frequency = frequency;
    }

    public short getScheduledDay() {
        return scheduledDay;
    }

    public void setScheduledDay(short scheduledDay) {
        this.scheduledDay = scheduledDay;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el gasto es relevante para la declaración de impuestos") Boolean getTaxRelated() {
        return isTaxRelated;
    }

    public void setTaxRelated(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el gasto es relevante para la declaración de impuestos") Boolean taxRelated) {
        isTaxRelated = taxRelated;
    }

    public TaxPurposeTypeEnum getTaxType() {
        return taxType;
    }

    public void setTaxType(TaxPurposeTypeEnum taxType) {
        this.taxType = taxType;
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

    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean deleted) {
        isDeleted = deleted;
    }
}