package com.example.OAuth.demo2;

import com.example.OAuth.demo2.Service.UserService;
import com.example.OAuth.demo2.domain.Role;
import com.example.OAuth.demo2.domain.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.sql.Timestamp;
import java.util.ArrayList;

@SpringBootApplication
public class Demo2Application{
	public static void main(String[] args) {
		SpringApplication.run(Demo2Application.class, args);
	}
	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
			System.out.println("Application Start");
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));

			userService.saveUser(new User(null, "sdhan", "hsd970730@naver.com", "1234", false,null, new ArrayList<>()));

			userService.addRoleToUser("sdhan", "ROLE_USER");
			userService.addRoleToUser("sdhan", "ROLE_ADMIN");
		};
	}
}
