package com.hp.proxy;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hp.common.DateUtil;
import com.hp.common.JsonUtil;
import com.hp.dao.FailInfoDao;
import com.hp.dao.model.FailInfoDo;
import com.hp.dao.model.IpWeight;
import com.hp.redis.RedisCache;
import com.hp.service.ProcessService;
import com.hp.service.ProxyService;
import com.hp.service.TestService;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.proxy.Proxy;
import us.codecraft.webmagic.proxy.ProxyProvider;

@Service
public class MyProxy implements ProxyProvider{
	@Autowired
	private RedisCache redisCache;
	@Autowired 
	public TestService testService;
	@Autowired 
	public ProxyService proxyService;
	
	@Value("${spider-ip-url}")
	private String ipUrl;
	
	@Autowired
	FailInfoDao failInfoDao;
	
	private  AtomicInteger count;
	
	@Override
	public void returnProxy(Proxy proxy, Page page, Task task) {
		// TODO Auto-generated method stub
		if(!page.isDownloadSuccess()) {
			String host=proxy.getHost();
			String port=proxy.getPort()+"";
			String json = redisCache.getString("IP");
			Set<IpWeight> set=new HashSet<>();
			Set<IpWeight> setIpPool = (Set<IpWeight>) JsonUtil.toBean(json, set.getClass());
			Iterator<IpWeight> it = setIpPool.iterator();
			AtomicInteger size=new AtomicInteger(setIpPool.size());
			for (AtomicInteger i = new AtomicInteger(1); i .get()<=size.get() ; i.incrementAndGet()) {
				IpWeight ipWeight= it.next();
				String proxyHost =ipWeight.getAddress();
				ipWeight.reduce();
				if(host.equals(proxyHost.split("-")[0])&&port.equals(proxyHost.split("-")[1])) {
					it.remove();
					ipWeight.reduce();
					FailInfoDo failInfoDo = new FailInfoDo();
					failInfoDo.setUrl(page.getRequest().getUrl());
					failInfoDo.setCreateTime(DateUtil.formatTo_yyyyMMdd());
					failInfoDo.setFailInfo("ip error");
					failInfoDao.insert(failInfoDo);
					if(ipWeight.getWeight()>0) {
						setIpPool.add(ipWeight);
					}
				}
			}
			redisCache.setString("IP2", JsonUtil.toJSONString(setIpPool));
			if(setIpPool.size()<1) {
				proxyService.ipWork(ipUrl);
			}
		}
		
	}

	@Override
	public Proxy getProxy(Task task) {
		String json = redisCache.getString("IP2");
		Set<IpWeight> set=new HashSet<>();
		Set<IpWeight> setIpPool = (Set<IpWeight>) JsonUtil.toBean(json, set.getClass());
		String proxyHost="";
		while(true) {
			Random r = new Random();
			count = new AtomicInteger(r.nextInt(setIpPool.size()));
			count.incrementAndGet();
			Iterator<IpWeight> it = setIpPool.iterator();
			
			for (AtomicInteger i = new AtomicInteger(1); i.get() <= count.get(); i.incrementAndGet()) {
				IpWeight ipWeight=it.next();
				proxyHost = ipWeight.getAddress();
			}
			if(proxyHost.split("-")[2].equals("http")||proxyHost.split("-")[2].equals("https")) {
				break;
			}
		}
		
		Proxy proxy=new Proxy(proxyHost.split("-")[0], Integer.valueOf(proxyHost.split("-")[1]));
		return proxy;
	}

}
