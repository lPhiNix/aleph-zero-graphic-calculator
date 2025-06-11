package com.alephzero.alephzero;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication @EnableCaching
public class AlephzeroApplication {
	public static void main(String[] args) {
		SpringApplication.run(AlephzeroApplication.class, args);
	}
}