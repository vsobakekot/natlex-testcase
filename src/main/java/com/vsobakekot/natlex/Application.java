package com.vsobakekot.natlex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/*
*
* NATLEX - Backend developer. Testcase REST application.
* @author Tema Grinevich  ::  nedvard@gmail.com
*
* */

@EnableAsync
@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
}
