package com.github.kmaeda.tutorial.consul.consulservice1;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableFeignClients
public class Consulservice1Application {

	public static void main(String[] args) {
		SpringApplication.run(Consulservice1Application.class, args);
	}

	@Bean
	public HttpMessageConverters httpMessageConverters() {
		return new HttpMessageConverters();
	}
}


