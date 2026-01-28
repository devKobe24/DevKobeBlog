package com.kobe.devkobeblog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class DevKobeBlogApplication {

    public static void main(String[] args) {
        SpringApplication.run(DevKobeBlogApplication.class, args);
    }

}
