package com.hp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.service.DownService;
import com.hp.service.TestService;

@RestController
//@SpringBootApplication(scanBasePackages = {"com.hp"})
public class SpiderController {
	@Autowired 
	public DownService downService;
	
	
	@RequestMapping("/down.do")
	public String f1() {
		//downService.down();
		return "down";
	}
	/*public static void main(String[] args) {
		SpringApplication.run(TestController.class, args);
	}*/
	
}
