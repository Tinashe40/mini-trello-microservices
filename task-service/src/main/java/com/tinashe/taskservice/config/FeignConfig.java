package com.tinashe.taskservice.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableFeignClients(basePackages = "com.tinashe.taskservice.feign")
public class FeignConfig {
  
}
