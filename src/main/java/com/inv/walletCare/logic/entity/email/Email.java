package com.inv.walletCare.logic.entity.email;

/**
 * Class designed to handle the information necessary for sending e-mails.
 */
public class Email {
    /**
     * Destination of the e-mail.
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
     * HTML e-mail template
     */
    private EmailTemplate template;

    public Email() {
    }

    public Email(String to, String copyTo, String subject, EmailTemplate template) {
        this.to = to;
        this.copyTo = copyTo;
        this.subject = subject;
        this.template = template;
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

    public EmailTemplate getTemplate() {
        return template;
    }

    public void setTemplate(EmailTemplate template) {
        this.template = template;
    }
}
