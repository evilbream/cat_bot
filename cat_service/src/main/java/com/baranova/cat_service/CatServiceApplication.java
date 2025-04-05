package com.baranova.cat_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.baranova.cat_service")
public class CatServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(CatServiceApplication.class, args);
	}

}
