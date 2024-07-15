package com.inv.walletCare.rest.user;

import com.inv.walletCare.logic.entity.email.Email;
import com.inv.walletCare.logic.entity.email.EmailSenderService;
import com.inv.walletCare.logic.entity.otp.OTPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RequestMapping("/password")
@RestController
public class PasswordController {
    @Autowired
    private OTPService otpService;
    @Autowired
    private EmailSenderService emailSenderService;

    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody Email email)throws Exception{
        String otp = otpService.generateOTP(email.getTo());

        Email emailDetails = new Email();
        emailDetails.setTo(email.getTo());
        emailDetails.setSubject("Your OTP Code");
        Map<String, String> params = new HashMap<>();
        params.put("otp", otp);

        emailSenderService.sendEmail(emailDetails, "ForgotPassword", params);

        return "Código OTP enviado";
    }
    @PostMapping("/validate-otp")
    public String validateOTP(@RequestParam String email, @RequestParam String otp) {
        if (otpService.validateOTP(email, otp)) {
            return "OTP Ingresado correctamente";
        } else {
            return "OTP Invalido";
        }
    }
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) throws Exception {
        if (otpService.validateOTP(email, otp)) {
            otpService.updatePassword(email, newPassword);
            return "Cotraseña actualizada";
        } else {
            return "OTP Invalido";
        }
    }





}
