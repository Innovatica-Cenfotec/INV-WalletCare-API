package com.inv.walletCare.datasource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Map;

@SpringBootTest
public class DatasourceAuthTest {

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
        registry.add("jasypt.encryptor.password", () -> "innova");
        registry.add("spring.datasource.url", () -> "jdbc:mariadb://localhost:3306/walletcare");
        registry.add("spring.datasource.username", () -> "innovatica");
        registry.add("spring.datasource.password", () -> "1nn0v4t1c4");
    }

    @Test
    void datasourceConnPrint() {
        System.out.println(printDatabaseConn());
    }
}
