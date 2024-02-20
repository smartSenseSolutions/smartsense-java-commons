package com.smartsensesolutions.java.commons.dao.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.smartsensesolutions", "com.smartsensesolutions.java.commons.dao.sample"})
public class CommonsDaoSampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommonsDaoSampleApplication.class, args);
    }
}
