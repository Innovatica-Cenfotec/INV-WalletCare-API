package com.inv.walletCare.rest.encryption;

import com.inv.walletCare.logic.entity.auth.encryption.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/encryption")
public class EncryptionController {
    @Autowired
    private EncryptionService encryptionService;

    @PostMapping("/encrypt")
    public String encrypt(@RequestBody String data) {
        try {
            return encryptionService.encrypt(data);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting data", e);
        }
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody String encryptedData) {
        try {
            return encryptionService.decrypt(encryptedData);
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting data", e);
        }
    }
}
