package com.orctom.bitflow.autumn;

import com.orctom.pipeline.Pipeline;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.e6wifi.bitflow.autumn"
})
public class AutumnApplication {

  public static void main(String[] args) {
    Pipeline.getInstance()
        .withApplicationName("autumn")
        .run(AutumnApplication.class);
  }
}
