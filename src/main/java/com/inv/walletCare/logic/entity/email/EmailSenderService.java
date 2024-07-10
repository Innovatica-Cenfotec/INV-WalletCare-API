package com.inv.walletCare.logic.entity.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.util.Map;

@Service
public class EmailSenderService {
    /**
     * Used for the configuration of e-mail sending
     */
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailTemplateRepository emailTemplateRepository;
    /**
     * Address used for sending e-mails
     */
    @Value("${spring.mail.username}")
    private String toBaseEmail;


    /**
     * This method get's ready the email to be sended
     * @param email it's all the email configuration
     * @param templateName it's the template's name in the database
     * @param params  are the keys in the HTML Template with its values
     * @throws Exception
     */
    public void sendEmail(Email email, String templateName, Map<String, String> params) throws Exception{

        var template = emailTemplateRepository.findByName(templateName);
        if(template.isEmpty()){
            throw new FileNotFoundException("Email Template could not be found.");
        }

        String emailBody = tokenReplacement(template.get().getTemplate(), params);
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
        return StringSubstitutor.replace(template, params, "${", "}");
    }

    /**
     * This method send the emails
     * @param email Information required for sending the e-mail
     * @throws MessagingException
     */
    private void sender(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(toBaseEmail));
        message.setRecipients(MimeMessage.RecipientType.TO, email.getTo());
        message.setSubject(email.getSubject());
        message.setContent(email.getBody(), "text/html; charset=utf-8");
        mailSender.send(message);
    }

}
