package com.hp.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.redis.RedisCache;
import com.hp.service.ProcessService;
import com.hp.service.ProxyService;
import com.hp.service.TestService;
import com.hp.service.impl.ProcessServiceImpl;


@RestController
public class HpWorkController {
	private static Logger logger = LoggerFactory.getLogger(HpWorkController.class);
	
	@Autowired
	private RedisCache redisCache; 
	
	@Autowired 
	public TestService testService;
	@Autowired 
	public ProxyService proxyService;
	
	@Autowired 
	public ProcessService processService;
	
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
	
	@RequestMapping("/hpwork.do")
	public String work() {
		long e=System.currentTimeMillis();
		if(startMode.substring(0,1).equals("0")) {
			for (String string : list) {
				processService.work(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime),startMode);
			}
		}else if(startMode.substring(0,1).equals("1")) {
			//获取代理生成代理池
			testService.ipWork(ipUrl);
			for (String string : list) {
				processService.workUseIp(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime),startMode);
			}
		}else if(startMode.substring(0,1).equals("2")){
			for (String string : list) {
				processService.webMagicWork(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
			}
		}else if(startMode.substring(0,1).equals("3")){
			//获取代理生成代理池
			proxyService.ipWork(ipUrl);
			for (String string : list) {
				processService.webMagicWorkUseIp(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
			}
		}
		long s=System.currentTimeMillis();
		logger.info("time:"+(s-e)/1000);
		return "ok";
		
	}
}
