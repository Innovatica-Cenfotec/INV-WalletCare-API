package com.inv.walletCare;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableEncryptableProperties
public class WalletCareApplication {

	public static void main(String[] args) {
		SpringApplication.run(WalletCareApplication.class, args);
	}
}
