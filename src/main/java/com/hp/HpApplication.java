package com.hp;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = "com.hp")
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
//public class HpApplication extends SpringBootServletInitializer{
public class HpApplication{


	
	

	public static void main(String[] args) {
	/*	SpringApplication newRun= new SpringApplication(HpApplication.class, args);   
      newRun.setBannerMode(Banner.Mode.OFF);  
        newRun.run(args);*/  
		SpringApplication.run(HpApplication.class, args);
		
	}
}
