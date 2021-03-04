package com.flappygo.jsontodart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.flappygo.jsontodart.Controller"})
public class JsontodartApplication {

    public static void main(String[] args) {
        SpringApplication.run(JsontodartApplication.class, args);
    }

}
