package com.hp;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication(scanBasePackages = "com.hp")
//public class HpApplication extends SpringBootServletInitializer{
public class HpApplication{
	/*@RequestMapping("/")
	String Home() {
		return "hello";
	}
	@RequestMapping("/home.do")
	String Home1() {
		return "home";
	}*/
	/*@Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.sources(HpApplication.class);
    }*/
	public static void main(String[] args) {
	/*	SpringApplication newRun= new SpringApplication(HpApplication.class, args);   
      newRun.setBannerMode(Banner.Mode.OFF);  
        newRun.run(args);*/  
		SpringApplication.run(HpApplication.class, args);
		
	}
}
