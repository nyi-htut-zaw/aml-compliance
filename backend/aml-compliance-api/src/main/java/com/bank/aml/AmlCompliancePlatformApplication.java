package com.bank.aml;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AML Compliance &amp; Financial Crime Platform — Main Application Entry Point
 *
 * <p><strong>DISCLAIMER:</strong> This is a demonstration platform. It is not
 * certified for production use, and does not constitute legal or regulatory
 * compliance advice. Consult qualified compliance professionals before
 * deploying in a regulated environment.</p>
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AmlCompliancePlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmlCompliancePlatformApplication.class, args);
    }
}
