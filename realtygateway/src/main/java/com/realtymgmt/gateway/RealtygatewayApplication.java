package com.realtymgmt.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

@EnableZuulProxy
@SpringBootApplication
public class RealtygatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(RealtygatewayApplication.class, args);
	}
	
}
