package com.inv.walletCare.logic.entity.notification;

public class NotificationResponse {
    /**
     * Email of the notification receiver.
     */
    private String receiverEmail;

    /**
     * Title of the notification.
     */
    private String type;

    /**
     * Title of the notification.
     */
    private String title;

    /**
     * Message of the notification.
     */
    private String message;


    // CONSTRUCTOR -------------------------------------------------------------------
    public NotificationResponse() {}


    // GET AND SETTERS ----------------------------------------------------------------

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
