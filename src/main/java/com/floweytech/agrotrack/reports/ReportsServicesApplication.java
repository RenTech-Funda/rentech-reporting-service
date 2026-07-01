package com.floweytech.agrotrack.reports;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReportsServicesApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportsServicesApplication.class, args);
    }

}
