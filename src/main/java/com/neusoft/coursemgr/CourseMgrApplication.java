package com.neusoft.coursemgr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CourseMgrApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseMgrApplication.class, args);
    }

}
