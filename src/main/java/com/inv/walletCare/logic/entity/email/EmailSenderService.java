package com.inv.walletCare.logic.entity.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.mail.MailMessage;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailSenderService  {
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
     * <p>This method is used for sending the e-mails</p>
     * @param email Information required for sending the e-mail
     * @throws MessagingException
     */
    public void sendEmail(Email email) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        message.setFrom(new InternetAddress(toBaseEmail));
        message.setRecipients(MimeMessage.RecipientType.TO, email.getTo());
        message.setSubject(email.getSubject());
        message.setContent(email.getTemplate(), "text/html; charset=utf-8");
        mailSender.send(message);
    }

}
