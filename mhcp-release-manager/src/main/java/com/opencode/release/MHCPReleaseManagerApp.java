package com.opencode.release;



import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;


@SpringBootApplication
@ConfigurationPropertiesScan
public class MHCPReleaseManagerApp {
    public static void main(String[] args) {
        SpringApplication.run(MHCPReleaseManagerApp.class, args);
    }
}