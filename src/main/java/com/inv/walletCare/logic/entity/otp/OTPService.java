package com.inv.walletCare.logic.entity.otp;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Class designed to generate the OTP
 * @author Jason Cruz
 */
@Service
public class OTPService {
    /**
     * Used to get users info and save changes
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * used to encode the new passwords
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Method used to generate the OTP Password and save it in the database setting the user in password recovery mode
     * @param user is the user information
     * @return returns the OTP password
     * @throws Exception is thrown in case the user email not exist
     */
    public String generateOTP(User user) throws Exception {

        String otp = UUID.randomUUID().toString().replace("_", "").substring(0, 8);

        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());
        if(optionalUser.isEmpty()){
            throw new Exception("User Email is not recognized to create OTP.");
        }
        optionalUser.map(existingUser->{
            existingUser.setName(user.getEmail());
            existingUser.setPassword(passwordEncoder.encode(otp));
            existingUser.setEmail(user.getEmail());
            //existingUser.setPasswordChangeRequired(true);
            return userRepository.save(existingUser);
        });
        return otp;
    }
}
