package com.noodle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class EcYYasuiApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(EcYYasuiApplication.class, args);
	}
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
	return application.sources(EcYYasuiApplication.class); }

}
