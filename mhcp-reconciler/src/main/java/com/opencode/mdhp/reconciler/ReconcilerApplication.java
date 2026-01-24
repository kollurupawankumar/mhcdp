package com.opencode.mdhp.reconciler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReconcilerApplication {
  public static void main(String[] args) {
    SpringApplication.run(ReconcilerApplication.class, args);
  }
}
