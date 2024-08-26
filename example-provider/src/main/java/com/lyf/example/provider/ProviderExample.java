package com.lyf.example.provider;

import com.lyf.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc
public class ProviderExample {
  public static void main(String[] args) {
    SpringApplication.run(ProviderExample.class, args);
  }
}
