package com.inv.walletCare.logic.entity.email;

/**
 * Class designed to handle the information necessary for sending e-mails.
 * @author Jason Cruz
 */
public class Email {
    /**
     * Destination of the e-mail.
     *
     */
    private String to;

    /**
     * Copy someone of the mail to be sent
     */
    private String copyTo;
    /**
     * E-mail subject
     */
    private String subject;
    /**
     * HTML e-mail body
     */
    private String body;

    public Email() {}

    public Email(String to, String copyTo, String subject, String body) {
        this.to = to;
        this.copyTo = copyTo;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCopyTo() {
        return copyTo;
    }

    public void setCopyTo(String copyTo) {
        this.copyTo = copyTo;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
