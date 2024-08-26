package com.lyf.example.consumer;

import com.lyf.annotation.EnableRpc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableRpc(needServer = false)
public class ConsumerExample {
  public static void main(String[] args) {
    SpringApplication.run(ConsumerExample.class, args);
  }
}
