package com.beigebigdata.pdf2htmlutil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class Pdf2htmlUtilApplication {

    public static void main(String[] args) {
        SpringApplication.run(Pdf2htmlUtilApplication.class, args);
    }
}
