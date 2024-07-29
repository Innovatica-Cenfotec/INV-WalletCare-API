package com.inv.walletCare.rest.user;

import com.inv.walletCare.logic.entity.response.Response;
import com.inv.walletCare.logic.entity.passwordReset.ResetPasswordRequest;
import com.inv.walletCare.logic.entity.email.Email;
import com.inv.walletCare.logic.entity.email.EmailSenderService;
import com.inv.walletCare.logic.entity.otp.OTPService;
import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This controller handles password-related operations such as
 * generating and validating OTPs, and resetting passwords.
 */
@RequestMapping("/password")
@RestController
public class PasswordController {
    @Autowired
    private OTPService otpService;

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private UserRepository userRepository;
    String otp;

    /**
     * Generates an OTP and sends it to the user's email.
     *
     * @param email The email address of the user requesting the OTP.
     * @return A message indicating that the OTP has been sent.
     * @throws Exception if there is an error during OTP generation or email sending.
     */
    @PostMapping("/forgot")
    public String forgotPassword(@RequestBody String email) throws Exception {
        otp = otpService.generateOTP(email);
        String resetPasswordLink = "http://localhost:4200/forgot-password-reset?email=" + email; // URL con el email como parámetro

        Email emailDetails = new Email();
        emailDetails.setTo(email);
        emailDetails.setSubject("Your OTP Code");
        Map<String, String> params = new HashMap<>();
        params.put("name", email);
        params.put("otp", otp);
        params.put("resetLink", resetPasswordLink); // Agrega el enlace a los parámetros

        emailSenderService.sendEmail(emailDetails, "ForgotPassword", params);

        return "Código OTP enviado";
    }
    @PostMapping("/change-password-otp")
    public String requestChangePassword() throws Exception {
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        User currentUser=(User) authentication.getPrincipal();
        otp = otpService.generateOTP(currentUser.getEmail());

        Email emailDetails = new Email();
        emailDetails.setTo(currentUser.getEmail());
        emailDetails.setSubject("Your OTP Code");
        Map<String, String> params = new HashMap<>();
        params.put("name", currentUser.getEmail());
        params.put("otp", otp);

        emailSenderService.sendEmail(emailDetails, "ChangePassword", params);

        return "Código OTP enviado";
    }
    /**
     * Validates the OTP entered by the user.
     *
     * @param user The user object containing the email and OTP.
     * @return A message indicating whether the OTP is valid or not.
     */
    @PostMapping("/validate-otp")
    public String validateOTP(@RequestBody User user) {
        if (otpService.validateOTP(user.getEmail(), user.getOtp())) {
            return "OTP Ingresado correctamente";
        } else {
            return "OTP Invalido";
        }
    }

    /**
     * Resets the user's password if the OTP is valid.
     *
     * @param resetPasswordRequest The request object containing email, OTP, and new password.
     * @return A message indicating whether the password has been updated or not.
     * @throws Exception if there is an error during the password update process.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<Response> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws Exception {
        if (otpService.validateOTP(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())) {
            otpService.updatePassword(resetPasswordRequest.getEmail(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok(new Response("Contraseña actualizada"));
        } else {
            throw new IllegalArgumentException("OTP inválido");
        }
    }
    @PostMapping("/change-password")
    public ResponseEntity<Response> changePassword(@RequestBody ResetPasswordRequest resetPasswordRequest) throws Exception {
        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        User currentUser=(User) authentication.getPrincipal();
        if (otpService.validateOTP(currentUser.getEmail(), resetPasswordRequest.getOtp())) {
            otpService.updatePassword(currentUser.getEmail(), resetPasswordRequest.getNewPassword());
            return ResponseEntity.ok(new Response("Contraseña actualizada"));
        } else {
            throw new IllegalArgumentException("OTP inválido");
        }
    }
}
