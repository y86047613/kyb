package com.zy.kyb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("application.yml")
public class KybApplication {

    public static void main(String[] args) {
        SpringApplication.run(KybApplication.class, args);
    }

}
