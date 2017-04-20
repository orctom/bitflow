package com.orctom.bitflow.winter;

import com.orctom.pipeline.Pipeline;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = {
    "com.e6wifi.bitflow.winter"
})
public class WinterApplication {

  public static void main(String[] args) {
    Pipeline.getInstance()
        .withApplicationName("winter")
        .run(WinterApplication.class);
  }
}
