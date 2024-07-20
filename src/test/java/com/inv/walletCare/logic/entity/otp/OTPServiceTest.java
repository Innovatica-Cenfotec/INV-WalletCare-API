//package com.inv.walletCare.logic.entity.otp;
//
//import com.inv.walletCare.logic.entity.user.User;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//@ExtendWith(SpringExtension.class)
//@SpringBootTest
//class OTPServiceTest {
//    @Autowired
//    OTPService otpService;
//
//    @Test
//    @DisplayName("Correct otp generation")
//    void generateOTP_CorrectInformation() {
//        try {
//            User user = new User();
//            user.setEmail("super.admin@gmail.com");
//            String generatedOTP = otpService.generateOTP(user);
//            System.out.println(String.format("OTP: %s", generatedOTP));
//        } catch (Exception e) {
//            Assertions.fail();
//        }
//    }
//
//    @Test
//    @DisplayName("Incorrect otp generation")
//    void generateOTP_IncorrectInformation() {
//        try {
//            User user = new User();
//            user.setEmail("super.admin@gmaill.com");
//            String generatedOTP = otpService.generateOTP(user);
//            System.out.println(String.format("OTP: %s", generatedOTP));
//        } catch (Exception e) {
//            Assertions.assertTrue(true);
//        }
//    }
//}