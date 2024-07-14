package com.inv.walletCare.logic.entity.auth.encryption;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class EncryptionServiceTest {
    @Autowired
    EncryptionService encryptionService;

    /**
     * Correct encryption and decryption configuration
     *
     * @throws Exception
     */
    @Test
    @DisplayName("Encrypt and decrypt data successfully")
    void testEncryptAndDecrypt(){
        try {
            String originalData = "SensitiveData123";
            String encryptedData = encryptionService.encrypt(originalData);
            String decryptedData = encryptionService.decrypt(encryptedData);

            Assertions.assertEquals(originalData, decryptedData, "Decrypted data should match the original data");
        } catch (Exception e) {
            Assertions.fail("Encrypt and decrypt data failed with exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Encrypt and decrypt empty data")
    void testEncryptAndDecryptEmptyData(){
        try {
            String originalData = "";
            String encryptedData = encryptionService.encrypt(originalData);
            String decryptedData = encryptionService.decrypt(encryptedData);

            Assertions.assertEquals(originalData, decryptedData,"Decrypted data should match the original data");
        } catch (Exception e) {
            Assertions.fail("Encryption or decryption empty data failed with exception: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Decrypt invalid data")
    void testDecryptInvalidData(){
        try {
            String invalidData = "This is invalid data";
            encryptionService.decrypt(invalidData);
            Assertions.fail("Decryption should have failed for invalid data");
        } catch (Exception e) {
            Assertions.assertTrue(true, "Exception was thrown as expected for invalid data");
        }
    }
}