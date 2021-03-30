package com.prodev.bloggingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BloggingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BloggingServiceApplication.class, args);
    }

}
