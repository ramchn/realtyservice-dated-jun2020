package com.realtymgmt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SignupApplication {

	
	// this is spring boot app
	public static void main(String[] args) {
		SpringApplication.run(SignupApplication.class, args);
		
	}

}
