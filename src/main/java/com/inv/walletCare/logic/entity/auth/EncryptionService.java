package com.inv.walletCare.logic.entity.auth;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

/**
 * Service class designed to encrypt and decrypt data using AES algorithm.
 * This class provides methods to securely encrypt and decrypt data, ensuring
 * confidentiality of sensitive information.
 *
 * @author Guillermo Parini
 */
@Service
public class EncryptionService {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    private final SecretKey secretKey;

    /**
     * Constructor for EncryptionService.
     * Initializes the AES key generator and generates a secret key with a length of 256 bits.
     *
     * @throws Exception if an error occurs during key generation
     */
    public EncryptionService() throws Exception {
        KeyGenerator keygen = KeyGenerator.getInstance(ALGORITHM);
        keygen.init(256);
        this.secretKey = keygen.generateKey();
    }

    /**
     * Encrypts the given data using AES algorithm.
     *
     * @param data the data to be encrypted
     * @return the encrypted data encoded in Base64 format
     * @throws Exception if an error occurs during encryption
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Decrypts the given encrypted data using AES algorithm.
     *
     * @param encryptedData the data to be decrypted, in Base64 encoded format
     * @return the decrypted data as a plain string
     * @throws Exception if an error occurs during decryption
     */
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        byte[] decrypted = cipher.doFinal(decoded);
        return new String(decrypted);
    }
}

