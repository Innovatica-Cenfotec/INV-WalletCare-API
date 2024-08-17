package com.inv.walletCare.logic.entity.expense;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.inv.walletCare.logic.entity.AmountTypeEnum;
import com.inv.walletCare.logic.entity.FrequencyTypeEnum;
import com.inv.walletCare.logic.entity.IncomeExpenceType;
import com.inv.walletCare.logic.entity.account.Account;
import com.inv.walletCare.logic.entity.tax.Tax;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.expenseCategory.ExpenseCategory;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

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
     * Owner of the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Account of the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", referencedColumnName = "id")
    private Account account;

    /**
     * Category associated with the expense.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "expense_category_id", referencedColumnName = "id")
    private ExpenseCategory expenseCategory;

    /**
     * Name of the expense
     */
    @Column(name = "name", length = 100, nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 100,
            message = "El nombre solo puede tener entre 4 y 100 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    /**
     * Description of the expense.
     */
    @Column(name = "description", length = 255)
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "La descripción debe tener menos de 255 caracteres")
    private String description;

    /**
     * Amount of the expense.
     */
    @Column(name = "amount", nullable = false)
    @Min(groups = {OnCreate.class, OnUpdate.class }, value = 0, message = "El monto del gasto debe ser mayor a 0")
    private BigDecimal amount;

    /**
     * Type of amount: net, gross.
     */
    @Column(name = "amount_type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El tipo de monto es requerido")
    private AmountTypeEnum amountType;

    /**
     * Indicates whether the expense is a template.
     */
    @Column(name = "is_template", nullable = false)
    @NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla")
    @JsonProperty(access = JsonProperty.Access.AUTO )
    private boolean isTemplate;

    /**
     * Type of expense: "unique" or "recurring".
     */
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido")
    private IncomeExpenceType type;

    /**
     * Frequency of the recurring: "monthly", "annual", "biweekly", "other" (applies only to recurring incomes).
     */
    @Column(name = "frequency", length = 50)
    @Enumerated(EnumType.STRING)
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
    private boolean isTaxRelated;

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
    private boolean isDeleted;

    /**
     * Flag to indicate whether to add to a transaction
     */
    @Transient
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private boolean addTransaction;


    // GET AND SETTERS ----------------------------------------------------------------

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ExpenseCategory getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(ExpenseCategory expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 100,
            message = "El nombre solo puede tener entre 4 y 100 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras y espacios") String getName() {
        return name;
    }

    public void setName(@NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 4, max = 100,
            message = "El nombre solo puede tener entre 4 y 100 caracteres") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras y espacios") String name) {
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

    public @Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del gasto debe ser mayor a 0") BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(@Min(groups = {OnCreate.class, OnUpdate.class}, value = 0, message = "El monto del gasto debe ser mayor a 0") BigDecimal amount) {
        this.amount = amount;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de monto es requerido") AmountTypeEnum getAmountType() {
        return amountType;
    }

    public void setAmountType(@NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "El tipo de monto es requerido") AmountTypeEnum amountType) {
        this.amountType = amountType;
    }

    @NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla")
    public boolean isTemplate() {
        return isTemplate;
    }

    public void setTemplate(@NotNull(groups = OnCreate.class, message = "Debe indicar si el gasto es una plantilla") boolean template) {
        isTemplate = template;
    }

    public @NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido") IncomeExpenceType getType() {
        return type;
    }

    public void setType(@NotNull(groups = OnCreate.class, message = "El tipo de gasto es requerido") IncomeExpenceType type) {
        this.type = type;
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
            message = "Debe indicar si el gasto es relevante para la declaración de impuestos")
    public boolean isTaxRelated() {
        return isTaxRelated;
    }

    public void setTaxRelated(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si el gasto es relevante para la declaración de impuestos") boolean taxRelated) {
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public boolean isAddTransaction() {
        return addTransaction;
    }

    public void setAddTransaction(boolean addTransaction) {
        this.addTransaction = addTransaction;
    }
}
