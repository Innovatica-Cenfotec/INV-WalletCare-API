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
     * Type of notification: "achievement", "goal" or "tip".
     */
    @Column(name = "type", length = 50)
    @Enumerated(EnumType.STRING)
    @NotNull(groups = {OnCreate.class, OnUpdate.class }, message = "El tipo de notificación es requerido.")
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

    /**
     * Get the notification id.
     * @return Long value with the notification id.
     */
    public @NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para buscar la notificación.") Long getId() {
        return id;
    }

    /**
     * Set the notification id with a Long.
     * @param id Long value for notification id.
     */
    public void setId(@NegativeOrZero(groups = OnUpdate.class,
            message = "El ID es requerido para buscar la notificación.") Long id) {
        this.id = id;
    }

    /**
     * Get the notification user set as owner.
     * @return User object with the notification owner.
     */
    public @NegativeOrZero(groups = OnUpdate.class,
            message = "Debe indicar el receptor de la notificación.") User getOwner() {
        return owner;
    }

    /**
     * Set the notification owner with a User object.
     * @param owner User object for notification owner.
     */
    public void setOwner(@NegativeOrZero(groups = OnUpdate.class,
            message = "Debe indicar el receptor de la notificación.") User owner) {
        this.owner = owner;
    }

    /**
     * Get notification type.
     * @return NotificationType value with the notification type.
     */
    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El tipo de notificación es requerido.") NotificationType getType() {
        return type;
    }

    /**
     * Set the notification type with a NotificationType enum.
     * @param type NotificationType object for notification type.
     */
    public void setType(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El tipo de notificación es requerido.") NotificationType type) {
        this.type = type;
    }

    /**
     * Get the notification title.
     * @return Notification title String.
     */
    public @NotNull(groups = {OnUpdate.class},
            message = "El titulo de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 1, max = 100,
            message = "El titulo de la notificación solo puede tener entre 1 y 100 caracteres.") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El titulo de la notificación solo puede contener letras, números y espacios.") String getTitle() {
        return title;
    }

    /**
     * Set the notification title with a String.
     * @param title String value with the notification title.
     */
    public void setTitle(@NotNull(groups = {OnUpdate.class},
            message = "El titulo de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, min = 1, max = 100,
            message = "El titulo de la notificación solo puede tener entre 1 y 100 caracteres.") @Pattern(groups = {OnCreate.class, OnUpdate.class}, regexp = "[a-zA-ZáéíóúüñÁÉÍÓÚÜÑ 0-9]+",
            message = "El titulo de la notificación solo puede contener letras, números y espacios.") String title) {
        this.title = title;
    }

    /**
     * Get the notification message.
     * @return Notification message String.
     */
    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El contenido de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "El contenido de la notificación debe tener menos de 255 caracteres.") String getMessage() {
        return message;
    }

    /**
     * Set the notification message with a String.
     * @param message String value with the notification message.
     */
    public void setMessage(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "El contenido de la notificación es requerido.") @Size(groups = {OnCreate.class, OnUpdate.class}, max = 255,
            message = "El contenido de la notificación debe tener menos de 255 caracteres.") String message) {
        this.message = message;
    }

    /**
     * Check if the notification is read.
     * True - is read. False - not read.
     * @return Notification isRead boolean.
     */
    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue leída.")
    public boolean isRead() {
        return isRead;
    }

    /**
     * Mark the notification as read or unread.
     * True - is read. False - not read.
     * @param read boolean value with the notification isRead.
     */
    public void setRead(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue leída.") boolean read) {
        isRead = read;
    }

    /**
     * Get the date of creation of the notification.
     * @return Datetime with the notification creation date.
     */
    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de creación de la notificación.") Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the notification creation date.
     * @param createdAt Datetime value with the notification creation date.
     */
    public void setCreatedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de creación de la notificación.") Date createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get the date of update of the notification.
     * @return Datetime with the notification update date.
     */
    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de modificación de la notificación.") Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Set the notification update date.
     * @param updatedAt Datetime value with the notification update date.
     */
    public void setUpdatedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar la fecha de modificación de la notificación.") Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Check if the notification is deleted.
     * True - deleted. False - not deleted.
     * @return Notification isDeleted boolean.
     */
    @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue eliminada.")
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Mark the notification as deleted.
     * True - deleted. False - not deleted.
     * @param deleted boolean value with the notification isDeleted.
     */
    public void setDeleted(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue eliminada.") boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * Get the date of delete of the notification.
     * @return Datetime with the notification delete date.
     */
    public @NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue borrada.") Date getDeletedAt() {
        return deletedAt;
    }

    /**
     * Set the notification delete date.
     * @param deletedAt Datetime value with the notification delete date.
     */
    public void setDeletedAt(@NotNull(groups = {OnCreate.class, OnUpdate.class},
            message = "Debe indicar si la notificación fue borrada.") Date deletedAt) {
        this.deletedAt = deletedAt;
    }
}

