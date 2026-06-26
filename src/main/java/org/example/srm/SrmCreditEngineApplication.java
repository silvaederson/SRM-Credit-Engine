package org.example.srm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "org.example.srm")
public class SrmCreditEngineApplication {
    public static void main(String[] args) {
        SpringApplication.run(SrmCreditEngineApplication.class, args);
    }
}