package com.inv.walletCare;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

@SpringBootTest
public class DecyptConnTest {

    @Value("${spring.datasource.password}")
    private String mariaPassword;

    @Value("${spring.datasource.url}")
    private String mariaURL;

    @Value("${spring.datasource.username}")
    private String mariaUser;

    public Map<String, String> printDatabaseConn() {
        return Map.of("apiURL", mariaURL, "apiPassword", mariaPassword, "apiUsername", mariaUser);
    }

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("jasypt.encryptor.password", () -> "my-secret-password");
        registry.add("spring.datasource.url", () -> "jdbc:mariadb://localhost:3306/walletcare");
        registry.add("jasypt.encryptor.property.prefix", () -> "[[[[[");
        registry.add("jasypt.encryptor.property.suffix", () -> "]]]]]");
    }

    @Test
    void datasourceConnPrint() {
        System.out.println(printDatabaseConn());
    }
}
