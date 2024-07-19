package com.inv.walletCare.logic.entity.email;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EmailSenderServiceTest {

    @Autowired
    private EmailSenderService emailSenderService;

    /**
     * Correct email configuration
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Send correct template")
    void sendEmail_Correcttemplate() {
        try {
            Email mail = new Email();
            mail.setTo("jscruzgz@gmail.com");
            mail.setSubject("Mail from unit test");
            Map<String, String> params = new HashMap<>();
            params.put("Name", "Jason Test");
            params.put("OTP", "789654123");
            emailSenderService.sendEmail(mail, "ForgotPassword", params);
        } catch (Exception e) {
            Assertions.fail();
        }

    }


    @Test
    @DisplayName("Send wrong template")
    void sendEmail_WrongTemplate() {
        try {
            Email mail = new Email();
            mail.setTo("jscruzgz@gmail.com");
            mail.setSubject("Mail from unit test");
            Map<String, String> params = new HashMap<>();
            params.put("Name", "Jason Test");
            params.put("OTP", "789654123");
            emailSenderService.sendEmail(mail, "ForgotPasswordd", params);
        } catch (Exception e) {
            Assertions.assertTrue(true);
        }

    }
}