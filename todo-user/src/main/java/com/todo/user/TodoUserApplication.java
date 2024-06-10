package com.todo.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
@EnableScheduling
public class TodoUserApplication {
//	http://localhost:7072/swagger-ui/index.html

	public static void main(String[] args) {
		SpringApplication.run(TodoUserApplication.class, args);
	}

}
