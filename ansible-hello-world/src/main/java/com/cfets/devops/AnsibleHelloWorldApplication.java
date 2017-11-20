package com.cfets.devops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class AnsibleHelloWorldApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(AnsibleHelloWorldApplication.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(applicationClass);
	}

	private static Class<AnsibleHelloWorldApplication> applicationClass = AnsibleHelloWorldApplication.class;
}
