package com.inv.walletCare.logic.entity.notification;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.validation.OnCreate;
import com.inv.walletCare.logic.validation.OnUpdate;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.Date;

/**
 * Represents a notification.
 */
@Entity
@Table(name = "notification")
public class Notification {
    /**
     * Unique identifier for the notification.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para buscar la notificación.")
    private Long id;

    /**
     * Owner of the notification.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    @NegativeOrZero(groups = OnUpdate.class,
            message = "Debe indicar el receptor de la notificación.")
    private User owner;

    /**
     * Type of notification: "system" or "custom".
     */
    @Column(name = "type", length = 50)
    private NotificationType type;

    /**
     * Title of the notification.
     */
    @Column(name = "title", length = 100, nullable = false)
    @NotNull(groups = {OnUpdate.class},
            message = "El titulo de la notificación es requerido.")
    @Size(groups = {OnCreate.class, OnUpdate.class }, min = 1, max = 100,
            message = "El titulo de la notificación solo puede tener entre 1 y 100 caracteres.")
    @Pattern(groups = {OnCreate.class, OnUpdate.class }, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El titulo de la notificación solo puede contener letras, números y espacios.")
    private String title;

    /**
     * Message of the notification.
     */
    @Column(name = "message")
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "El contenido de la notificación es requerido.")
    @Size(groups = {OnCreate.class, OnUpdate.class }, max = 255,
            message = "El contenido de la notificación debe tener menos de 255 caracteres.")
    private String message;

    /**
     * Flag indicating whether the notification is read.
     */
    @Column(name = "is_read", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar si la notificación fue leída.")
    private boolean isRead;

    /**
     * Datetime when the notification was created.
     */
    @Column(name = "created_at", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar la fecha de creación de la notificación.")
    private Date createdAt;

    /**
     * Datetime when the notification was last updated.
     */
    @Column(name = "updated_at", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar la fecha de modificación de la notificación.")
    private Date updatedAt;

    /**
     * Flag indicating whether the notification is deleted.
     */
    @Column(name = "is_deleted", nullable = false)
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar si la notificación fue eliminada.")
    private boolean isDeleted;

    /**
     * Datetime when the notification was deleted.
     */
    @Column(name = "deleted_at")
    @NotNull(groups = {OnCreate.class, OnUpdate.class },
            message = "Debe indicar si la notificación fue borrada.")
    private Date deletedAt;

    // CONSTRUCTOR -------------------------------------------------------------------
    public Notification() {}

    // GET AND SETTERS ----------------------------------------------------------------
    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para buscar la notificación.") Long getId() {
        return id;
    }

    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para buscar la notificación.") Long id) {
        this.id = id;
    }

    public @NegativeOrZero(groups = OnUpdate.class,
            message = "Debe indicar el receptor de la notificación.") User getOwner() {
        return owner;
    }

    public void setOwner(@NegativeOrZero(groups = OnUpdate.class,
            message = "Debe indicar el receptor de la notificación.") User owner) {
        this.owner = owner;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public @NotNull(groups = {OnUpdate.class},
            message = "El titulo de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 1, max = 100,
            message = "El titulo de la notificación solo puede tener entre 1 y 100 caracteres.") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El titulo de la notificación solo puede contener letras, números y espacios.") String getTitle() {
        return title;
    }

    public void setTitle(@NotNull(groups = {OnUpdate.class},
            message = "El titulo de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 1, max = 100,
            message = "El titulo de la notificación solo puede tener entre 1 y 100 caracteres.") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El titulo de la notificación solo puede contener letras, números y espacios.") String title) {
        this.title = title;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El contenido de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "El contenido de la notificación debe tener menos de 255 caracteres.") String getMessage() {
        return message;
    }

    public void setMessage(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El contenido de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "El contenido de la notificación debe tener menos de 255 caracteres.") String message) {
        this.message = message;
    }

    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue leída.")
    public boolean isRead() {
        return isRead;
    }

    public void setRead(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue leída.") boolean read) {
        isRead = read;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de creación de la notificación.") Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de creación de la notificación.") Date createdAt) {
        this.createdAt = createdAt;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de modificación de la notificación.") Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de modificación de la notificación.") Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue eliminada.")
    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue eliminada.") boolean deleted) {
        isDeleted = deleted;
    }

    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue borrada.") Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue borrada.") Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}

