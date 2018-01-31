package com.hp.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HpController {
		/*@RequestMapping("/")
		String Home() {
			return "hello";
		}*/
		@RequestMapping(value="hello", method=RequestMethod.GET)
		String Home1() {
			return "home";
		}
	
	
}
