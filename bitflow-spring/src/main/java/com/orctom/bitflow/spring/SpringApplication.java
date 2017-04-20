package com.orctom.bitflow.spring;

import com.orctom.pipeline.Pipeline;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.e6wifi.bitflow.spring"
})
public class SpringApplication {

  public static void main(String[] args) {
    Pipeline.getInstance()
        .withApplicationName("spring")
        .run(SpringApplication.class);
  }
}
