package com.smartsensesolutions.commons.dao.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.smartsensesolutions"})
public class CommonsDaoSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonsDaoSampleApplication.class, args);
    }
}
