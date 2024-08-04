package com.inv.walletCare.logic.entity.notification;

/**
 * Object to transfer notification details to notification service.
 */
public class NotificationDTO {
    /**
     * Email of the notification receiver.
     */
    private String receiverEmail;

    /**
     * Title of the notification.
     */
    private NotificationType type;

    /**
     * Title of the notification.
     */
    private String title;

    /**
     * Message of the notification.
     */
    private String message;


    // CONSTRUCTOR -------------------------------------------------------------------
    public NotificationDTO() {}


    // GET AND SETTERS ----------------------------------------------------------------

    /**
     * Get email of the registered user the will receive the notification.
     * @return String value with the receiver email.
     */
    public String getReceiverEmail() {
        return receiverEmail;
    }

    /**
     * Set the email of the registered user to receive the notification.
     * @param receiverEmail String value with the receiver email.
     */
    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    /**
     * Get notification type.
     * @return NotificationType value with the notification type.
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Set the notification type with a NotificationType enum.
     * @param type NotificationType object for notification type.
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Get the notification title.
     * @return Notification title String.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the notification title with a String.
     * @param title String value with the notification title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the notification message.
     * @return Notification message String.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the notification message with a String.
     * @param message String value with the notification message.
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
