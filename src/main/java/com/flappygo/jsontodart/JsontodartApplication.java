package com.flappygo.jsontodart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@ComponentScan({"com.flappygo.jsontodart.Controller","com.flappygo.jsontodart.Config"})
public class JsontodartApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsontodartApplication.class, args);
    }

}
