package com.junydev.spring.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
public class SpringFrameworkApi {

    public static void main(String[] args) {
        SpringApplication.run(SpringFrameworkApi.class);
    }
}