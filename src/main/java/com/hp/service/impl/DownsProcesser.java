package com.hp.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

import com.hp.dao.DownDao;
import com.hp.dao.model.DownDO;

import us.codecraft.webmagic.Page;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;


@Component
@SpringBootApplication  
public class DownsProcesser implements PageProcessor{
	//public static Set<String> set = new HashSet();
	public static Logger logger = LoggerFactory.getLogger(DownsProcesser.class);
	//列表页
	public static final String URL_LIST = "by_sub_category\\.php\\?id=226985&name=%E4%B8%9C%E4%BA%AC%E9%A3%9F%E5%B0%B8%E9%AC%BC\\+%E5%A3%81%E7%BA%B8&lang=Chinese&page=[0-3]";
	//详情页
	public static final String URL_POST = "https://wall\\.alphacoders\\.com/big.php\\?i=\\d{6}&lang=Chinese";
	//原图
	public static final String IMAGE = "https://images3\\.alphacoders\\.com/\\d{3}/\\d{6}\\.png";

	private Site site = new Site().me().setRetryTimes(3).setTimeOut(5000000).setSleepTime(1000).addHeader("User-Agent",
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.516.400 QQBrowser/9.4.8188.400");
	
	@Autowired
	DownDao downDao;
	
	
	@Override
	public Site getSite() {
		// TODO Auto-generated method stub
		return site;
	}

	@Override
	public void process(Page page) {
		page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());

		if (page.getUrl().regex(URL_LIST).match()) {
			page.addTargetRequests(page.getHtml().links().regex(URL_POST).all());
		} else {
		/*	Request r=new Request();
			//r.set
			page.addTargetRequest(r);*/
			if(null!=page.getHtml().xpath("//div[@style='width:100%;padding:10px;max-width:975px;margin:auto;']/div[@class='row']/div/span/@data-href")) {
			String name=page.getHtml().xpath("//div[@style='width:100%;padding:10px;max-width:975px;margin:auto;']/div[@class='row']/div/span/@data-href").toString();
			if(null!=name) {
			//System.out.println(name);
			//downDao.insert(name);
	/*	DownDO downDO=new DownDO();
		downDO.setName(name);*/
				logger.info("log:"+name);
				downDao.insert(name);
				
		//page.putField("name", downDO);
			}
			}
		}
		
	}

}
