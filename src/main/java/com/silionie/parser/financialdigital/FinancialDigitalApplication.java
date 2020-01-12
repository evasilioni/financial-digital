package com.silionie.parser.financialdigital;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class FinancialDigitalApplication {

	public static void main(String[] args) {
		SpringApplication.run(FinancialDigitalApplication.class, args);
	}

	@Bean
	@Primary
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
