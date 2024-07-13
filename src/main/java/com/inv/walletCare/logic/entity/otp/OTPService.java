package com.inv.walletCare.logic.entity.otp;

import com.inv.walletCare.logic.entity.user.User;
import com.inv.walletCare.logic.entity.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OTPService {
    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder psEncoder;

    public OTPService(PasswordEncoder psEncoder) {
        this.psEncoder = psEncoder;
    }

    public String generateOTP(User user) throws Exception{
        String rndString = UUID.randomUUID().toString().replace("_", "");
        String otp = rndString.substring(0, 8);
        var userFound = userRepository.findByEmail(user.getEmail());

        if(userFound.isEmpty()){
            throw new Exception("User Email not found");
        }
        user.setPassword(psEncoder.encode(otp));

        //userRepository.save(user);

        return otp;
    }
}
