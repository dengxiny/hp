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

import com.hp.dao.LianJiaDao;
import com.hp.dao.TestDao;
import com.hp.dao.model.LianJiaDO;
import com.hp.redis.RedisCache;
import com.hp.service.ProcessService;
import com.hp.service.TestService;




@RestController
//@SpringBootApplication(scanBasePackages = {"com.hp"})
public class TestController2 {
	@Autowired
	public RedisCache redisCache; 
	
	@Autowired 
	public TestService testService;
	
	@Autowired 
	public ProcessService processService;
	@Autowired
	LianJiaDao lianJiaDao;
	
	@Value("#{'${spider-list}'.split(',')}")
	private List<String> list;
	
	@Value("${spider-ip-url}")
	private String ipUrl;
	
	@Value("${spider-thread-size}")
	private String threadSize;
	
	@Value("${spider-thread-sleep-time}")
	private String threadSleepTime;
	
	@Value("${start-mode}")
	private String startMode;
	
	@RequestMapping("/work.do")
	public String f1() {
		long e=System.currentTimeMillis();
		//testService.ipWork(ipUrl);
		//for (String string : list) {
			//processService.work(list.get(0),Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
		//}
		
		/*for (String string : list) {
			testService.testUrl(string);
		}*/
		/*	if(startMode.equals("0")) {
				processService.work(list.get(0),Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
			}else {
				//testService.ipWork(ipUrl);
				//for (String string : list) {
					processService.workUseIp(list.get(0),Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
				//}
			}
		long s=System.currentTimeMillis();
		System.out.println("time:"+(s-e)/1000);	*/
		//redisCache.setString("URL", list.get(0));
		//processService.webMagicWork(list.get(0),Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
		return "ok";
		
	}
	/*public static void main(String[] args) {
		SpringApplication.run(TestController.class, args);
	}*/
	@RequestMapping("/error.do")
	public List<String> f2() {
		LianJiaDO l=new LianJiaDO();
		return lianJiaDao.selectErrorUrlList(l);
	}
}
