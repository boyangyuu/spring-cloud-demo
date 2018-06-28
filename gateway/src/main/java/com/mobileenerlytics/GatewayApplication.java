package com.mobileenerlytics;

import com.mobileenerlytics.filter.pre.SimpleFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableEurekaClient
@EnableZuulProxy
public class GatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
		System.out.println("gateway-microservice is running");
	}

	@Bean
	public SimpleFilter simpleFilter() {
		return new SimpleFilter();
	}
}
