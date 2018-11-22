package com.hp.service.impl;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hp.redis.RedisCache;
import com.hp.service.ProcessService;
import com.hp.service.TestService;

import us.codecraft.webmagic.Spider;




@Component
@EnableScheduling
public class TimeDownServiceImpl {
	public static Logger logger = LoggerFactory.getLogger(TimeDownServiceImpl.class);
	
	@Resource
	private DownsProcesser downsProcesser;
	
	@Autowired
	private RedisCache redisCache; 
	
	@Autowired 
	private TestService testService;
	
	@Autowired 
	private ProcessService processService;
	
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
	
	
	@Value("${quartz-mode}")
	private String quartzMode;
	
	
	
	//阻塞标志位
	public static int a;
	//定时任务测试
/*	@Scheduled(fixedRate = 5000)//每5秒执行一次
	public void play() throws Exception {
        System.out.println("执行Quartz定时器任务："+new Date());
    }
	
	//@Value("${quartz-schedule}")
	@Scheduled(cron = "${quartz-schedule}")
	public void changeIpPool() {
		System.out.println("changeIpPool");
	}
	*/ 
	/*//每日10点定时跑 过10分钟更换一次IP池
	@Scheduled(cron = "0 21 10 * * ?")
	public void changeIpPool() {
		
	}*/
	
	//每日10点定时跑
	/*@Scheduled(cron = "0 11 10 1/3 * ?") 
	public void down() {
		a=0;
		long es=System.currentTimeMillis();
		Spider spider=Spider.create(downsProcesser).addUrl("https://wall.alphacoders.com/by_sub_category.php?id=226985&name=%E4%B8%9C%E4%BA%AC%E9%A3%9F%E5%B0%B8%E9%AC%BC+%E5%A3%81%E7%BA%B8&lang=Chinese")
				.thread(3);
		spider.start();
		//传统方法 读取spider状态 阻塞防止直接返回对异常进行处理
		new Thread(() ->{
        	while(true){
					try {
						
						Thread.sleep(2000);
					if(spider.getStatus().toString().equals("Stopped")){
						a=1;
		            	System.out.println("完毕");
		            	break;
		            }else if(spider.getStatus().toString().equals("running")){
		            	System.out.println("休息");
		            }else {
		            	System.out.println("休息");
		            }
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        	}
			}) .start();
		while (true) {
			
		if(a==1) {
			long ee=System.currentTimeMillis();
			System.out.println(ee-es+" time");
			break;
		}else {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		}
		
		//callable future 来 读取spider状态 阻塞防止直接返回对异常进行处理
		ExecutorService threadPool = Executors.newSingleThreadExecutor();
        Future<Integer> future = threadPool.submit(new Callable<Integer>() {
            public Integer call() throws Exception {
            	while(true){
					try {
					Thread.sleep(2000);
					if(spider.getStatus().toString().equals("Stopped")){
						a=1;
		            	System.out.println("完毕");
		            	break;
		            }else if(spider.getStatus().toString().equals("running")){
		            	System.out.println("休息");
		            	spider.close();
		            }else {
		            	System.out.println("休息");
		            }
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
               
            		}
            	 return a;
            	}
        });
        try {
            Thread.sleep(5000);// 可能做一些事情
            if(future.get()==1) {
    			long ee=System.currentTimeMillis();
    			System.out.println(ee-es+" ms");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
	}*/
	@Scheduled(cron = "${quartz-schedule}")
	public void run() {
		if(quartzMode.equals("0")) {
			logger.info("=======定时开关关闭=======");
		}else {
			logger.info("=======定时任务开启=======");
			long e=System.currentTimeMillis();
			if(startMode.substring(0,1).equals("0")) {
				for (String string : list) {
					processService.work(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime),startMode);
				}
			}else if(startMode.substring(0,1).equals("1")) {
				testService.ipWork(ipUrl);
				for (String string : list) {
					processService.workUseIp(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime),startMode);
				}
			}else {
				for (String string : list) {
				processService.webMagicWork(string,Integer.valueOf(threadSize),Long.valueOf(threadSleepTime));
				}
			}
			long s=System.currentTimeMillis();
			logger.info("time:"+(s-e)/1000);
			logger.info("=======定时任务结束=======");
		}
	}
}
