package com.github.kmaeda.springcloud.springcloudservice1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Springcloudservice1Application {

	public static void main(String[] args) {
		SpringApplication.run(Springcloudservice1Application.class, args);
	}

	@Bean
	public HttpMessageConverters httpMessageConverters() {
		return new HttpMessageConverters();
	}
}
