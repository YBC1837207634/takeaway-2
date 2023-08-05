package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching  // 开启缓存注解功能
public class Takeaway1Application {

    public static void main(String[] args) {
        SpringApplication.run(Takeaway1Application.class, args);
    }

}
