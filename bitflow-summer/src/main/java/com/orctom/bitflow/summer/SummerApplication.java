package com.orctom.bitflow.summer;

import com.orctom.pipeline.Pipeline;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.e6wifi.bitflow.summer"
})
public class SummerApplication {

  public static void main(String[] args) {
    Pipeline.getInstance()
        .withApplicationName("summer")
        .run(SummerApplication.class);
  }
}
