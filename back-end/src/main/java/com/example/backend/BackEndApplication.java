package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = "com.example.backend")
@EnableJpaRepositories(basePackages = "com.example.backend")
public class BackEndApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackEndApplication.class, args);
    }
}
