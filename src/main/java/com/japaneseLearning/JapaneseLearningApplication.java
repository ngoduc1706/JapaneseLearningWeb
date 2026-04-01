package com.japaneseLearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.japaneseLearning")
public class JapaneseLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(JapaneseLearningApplication.class, args);
    }
}
