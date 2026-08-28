package com.g14.pindan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 校园拼单/组队平台启动类
 */
@EnableScheduling
@SpringBootApplication
public class PindanApplication {

    public static void main(String[] args) {
        SpringApplication.run(PindanApplication.class, args);
    }
}
