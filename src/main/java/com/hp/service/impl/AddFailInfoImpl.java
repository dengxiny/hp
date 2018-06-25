package com.hp.service.impl;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hp.common.JsonUtil;
import com.hp.redis.RedisCache;

@Service
public class AddFailInfoImpl {
	private static Logger logger = Logger.getLogger(AddFailInfoImpl.class);
	
	
	@Autowired
	private RedisCache redisCache;
	
	private ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
	
	public int removeIP(String ip) {
		try {
			Set<String> set=new HashSet<String>();
			Lock lock=new ReentrantLock(); 
			lock.lock();
			String json=redisCache.getString("IP");
			Set<String> set1=(Set<String>) JsonUtil.toBean(json,set.getClass());
			set1.remove(ip);
			redisCache.setString("IP", JsonUtil.toJSONString(set1));
			lock.unlock();
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		return 1;
	} 
	
	public int addFailUrl(String url) {
		try {
			 cachedThreadPool.execute(new Runnable() {
		            public void run() {
		                //sendMessage();
		            }
		        });
			//使用本地IP 如果错误加入数据 然后更换IP
			//dao.add
		} catch (Exception e) {
			// TODO: handle exception
			return 0;
		}
		return 1;
	}
	
	public void work(String url) {
		Boolean flag=false;
		Set<String> set=new HashSet<String>();
		String json=redisCache.getString("IP");
		set=(Set<String>) JsonUtil.toBean(json,set.getClass());
		Iterator<String> itera=set.iterator();
		while(itera.hasNext()) {
			String ip=itera.next();
			int i=4;
			while(i>0) {
				try {
					//
				} catch (Exception e) {
				}
			/*	if() {
					i--;
				}else {
					flag=true;
				}*/
			}
			if(!flag==true) {
				try {
					removeIP(ip);
				} catch (Exception e) {
				}
			}else {
				break;
			}
		} 
		if(!flag==true) {
			try {
				addFailUrl(url);
			} catch (Exception e) {
			}
			
		}
		
	} 
}
