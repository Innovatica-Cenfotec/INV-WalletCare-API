package com.inv.walletCare.logic.entity.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * @author Jason Cruz
 * Class designed to send emails
 */
@Service
public class EmailSenderService {
    /**
     * Used for the configuration of e-mail sending
     */
    @Autowired
    private JavaMailSender mailSender;


    /**
     * Address used for sending e-mails
     */
    @Value("${spring.mail.username}")
    private String toBaseEmail;

    /**
     * Path to the HTML Templates
     */
    private final String PATH_TO_TEMPLATES = "src/main/resources/templates/";

    /**
     * This method gets ready the email to be sent
     * @param email it's all the email configuration
     * @param templateName it's the template's name in the database
     * @param params  are the keys in the HTML Template with its values
     * @throws Exception can throw two exceptions
     *  1. The email template name is not found
     *  2. The email can't be sent
     */
    public void sendEmail(Email email, String templateName, Map<String, String> params) throws Exception {
        var template = getTemplate(templateName);
        String emailBody = tokenReplacement(template, params);
        email.setBody(emailBody);
        sender(email);
    }

    /**
     * This method replace the tokens in the HTML Template for the final values.
     * @param template is the HTML Template
     * @param params are the keys in the HTML Template with its values
     * @return returns the final HTML that will be the email body
     */
    private String tokenReplacement(String template, Map<String, String> params){
        return StringSubstitutor.replace(template, params, "{{", "}}");
    }

    /**
     * This method send the emails
     * @param email Information required for sending the e-mail
     * @throws MessagingException the exception is thrown if the email could not be sent
     */
    private void sender(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(toBaseEmail));
        message.setRecipients(MimeMessage.RecipientType.TO, email.getTo());
        message.setSubject(email.getSubject());
        message.setContent(email.getBody(), "text/html; charset=utf-8");
        mailSender.send(message);
    }

    /**
     * This method gets the email template from the templates folder
     * @param templateName is the html template name
     * @return returns the HTML template
     * @throws Exception the exception is thrown only if the template name not exists in the templates folder
     */
    private String getTemplate(String templateName) throws Exception {

        String templatePath = PATH_TO_TEMPLATES+templateName+".html";
        File file = new File(templatePath);
        if(!file.exists()){
            throw new FileNotFoundException("Email Template could not be found.");
        }

        return Files.readString(Paths.get(templatePath));
    }

}
