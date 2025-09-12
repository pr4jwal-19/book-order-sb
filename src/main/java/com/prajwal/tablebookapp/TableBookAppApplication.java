package com.prajwal.tablebookapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TableBookAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(TableBookAppApplication.class, args);
    }

}
