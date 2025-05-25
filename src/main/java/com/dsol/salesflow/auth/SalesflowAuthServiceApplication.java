package com.dsol.salesflow.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.dsol.salesflow.auth", "com.dsol.salesflow.asset"})
public class SalesflowAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SalesflowAuthServiceApplication.class, args);
	}

}
