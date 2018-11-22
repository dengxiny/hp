package com.hp.service.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.hp.dao.TestDao;
import com.hp.dao.model.TestDO;
import com.hp.service.DownService;
import com.hp.service.TestService;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;





@Service
public class DownServiceImpl implements DownService{
	public static Logger logger = LoggerFactory.getLogger(DownServiceImpl.class);
/*	@Resource
	TestDao testdao;*/
	@Resource
	private DownsProcesser downsProcesser;
	
	//阻塞标志位
	public static int a;
	//new DownsProcesser()
	@Override
	public void down() {
		a=0;
		//Date date=new Date();
		long es=System.currentTimeMillis();
		Spider spider=Spider.create(downsProcesser).addUrl("https://wall.alphacoders.com/by_sub_category.php?id=226985&name=%E4%B8%9C%E4%BA%AC%E9%A3%9F%E5%B0%B8%E9%AC%BC+%E5%A3%81%E7%BA%B8&lang=Chinese")
			//	.addPipeline(new downDaoPipeline())
				.thread(3);
		spider.start();
		//传统方法 读取spider状态 阻塞防止直接返回对异常进行处理
		/*new Thread(() ->{
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
		}*/
		
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
    			System.out.println(ee-es+" time");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
	}

}
