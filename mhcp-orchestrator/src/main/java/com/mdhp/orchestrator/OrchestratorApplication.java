package com.opencode.mdhp.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.UUID;

@SpringBootApplication
public class OrchestratorApplication {


  public static void main(String[] args) {
    SpringApplication.run(OrchestratorApplication.class, args);
  }

}
