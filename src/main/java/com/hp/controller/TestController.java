package com.hp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.dao.model.TestDO2;
import com.hp.service.DownService;
import com.hp.service.TestService;


@RestController
//@SpringBootApplication(scanBasePackages = {"com.hp"})
public class TestController {
	@Autowired 
	public TestService testService;
	@Value("#{'${spider-list}'.split(',')}")
	private List<String> list;
	
	@RequestMapping("/test.do")
	public List<TestDO2> f1() {
		//testService.testUrl("https://hz.lianjia.com/ershoufang/103102041770.html");
		List<TestDO2> list1=new ArrayList<TestDO2>();
		for (String string : list) {
			System.out.println(string);
			TestDO2 t=new TestDO2();
			t.setName(string.split("-")[0]);
			t.setRename(string.split("-")[1]);
			list1.add(t);
		}
		return list1;
		
	}
	/*public static void main(String[] args) {
		SpringApplication.run(TestController.class, args);
	}*/
	
}
