package com.placeholder.placeholder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication @EnableCaching
public class PlaceholderApplication {
	public static void main(String[] args) {
		SpringApplication.run(PlaceholderApplication.class, args);
	}
}