package com.inv.walletCare.logic.entity.auth.encryption;

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

    @Test
    @DisplayName("Correct Encryption")
    void encrypt_corret(){

    }
}