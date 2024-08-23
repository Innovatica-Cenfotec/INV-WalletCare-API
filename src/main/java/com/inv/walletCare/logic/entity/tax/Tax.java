package com.inv.walletCare.logic.entity.tax;

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

/**
 * Represents a tax that can be applied to expenses.
 */
@Table(name = "tax")
@Entity
public class Tax {
    /**
     * Unique identifier for the tax.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un impuesto")
    private Long id;

    /**
     * Owner of the tax.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id",  referencedColumnName = "id", nullable = false)
    private User owner;

    /**
     * Name of the tax
     */
    @Column(name = "name", length = 50, nullable = false)
    @NotNull(groups = {OnUpdate.class}, message = "El nombre es requerido")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 4, max = 100,
            message = "El nombre solo puede tener entre 4 y 100 caracteres")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El nombre solo puede contener letras y espacios")
    private String name;

    /**
     * Description of the tax.
     */
    @Column(name = "description", length = 255)
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "La descripción debe tener menos de 255 caracteres")
    private String description;

    /**
     * Percentage of the tax.
     */
    @Column(name = "percentage", nullable = false)
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 0, max = 100,
            message = "El porcentaje del impuesto debe estar entre 0 y 100")
    private BigDecimal percentage;

    /**
     * Datetime when the tax was created.
     */
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    /**
     * Datetime when the tax was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    private Date updatedAt;

    /**
     * Datetime when the tax was deleted (can be null if the tax is not deleted).
     */
    @Column(name = "deleted_at")
    private Date deletedAt;

    /**
     * Flag to indicate if the tax is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted;

    public @NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un impuesto") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class, message = "El ID es requerido para actualizar un impuesto") Long id) {
        this.id = id;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
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

    public @Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, max = 100,
            message = "El porcentaje del impuesto debe estar entre 0 y 100") BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(@Size(groups = {OnCreate.class, OnUpdate.class}, min = 0, max = 100,
            message = "El porcentaje del impuesto debe estar entre 0 y 100") BigDecimal percentage) {
        this.percentage = percentage;
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