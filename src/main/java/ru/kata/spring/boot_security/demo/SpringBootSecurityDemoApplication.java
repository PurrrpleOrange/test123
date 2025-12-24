package ru.kata.spring.boot_security.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SpringBootSecurityDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootSecurityDemoApplication.class, args);
		String hashFromDb = "$2a$10$a3vofdDr17mQe/O752/VP.h9005JCrmr8NwJ1rLh3gxdlaieToDaS";
		BCryptPasswordEncoder enc = new BCryptPasswordEncoder();

		System.out.println(enc.matches("admin", hashFromDb));
		System.out.println(enc.matches("password", hashFromDb));
	}

}
