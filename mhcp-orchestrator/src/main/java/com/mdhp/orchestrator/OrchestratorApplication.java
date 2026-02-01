package com.mdhp.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.mdhp.orchestrator",
        "com.mdhp.common"
})
public class OrchestratorApplication {


  public static void main(String[] args) {
    SpringApplication.run(OrchestratorApplication.class, args);
  }

}
