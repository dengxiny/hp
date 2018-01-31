package com.hp.service.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hp.common.DateUtil;
import com.hp.common.LianJiaCostant;
import com.hp.dao.FailInfoDao;
import com.hp.dao.LianJiaDao;
import com.hp.dao.model.FailInfoDo;
import com.hp.dao.model.LianJiaDO;
import com.hp.redis.RedisCache;

import us.codecraft.webmagic.Page;

import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;


@Component
public class WorkProcesser implements PageProcessor{
	
	public static Logger logger = LoggerFactory.getLogger(WorkProcesser.class);
	
	@Autowired
	public  RedisCache redisCache;
	
	@Autowired
	LianJiaDao lianJiaDao;
	
	@Autowired
	FailInfoDao failInfoDao;

	//列表页
	public  String URL_LIST="";//redisCache.getString("URL").split("lianjia.com/")[1]+"/pg\\d{1}";
	//详情页
	public  String URL_POST ="";//redisCache.getString("URL").split("lianjia.com/")[0]+"lianjia.com/"+redisCache.getString("URL").split("lianjia.com/")[1].split("/")[0]+"\\d+.html";
	public  String URL_DETAIL="";//URL_POST.replace(".", "\\.");
	public int count=0;
	
	@Value(value="${spider-thread-sleep-time}")
	public  String threadSleepTime;
	//Integer.valueOf(threadSleepTime)
	private Site site = new Site().me().setRetryTimes(3).setTimeOut(30000).addHeader("User-Agent",
			"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/47.0.2526.80 Safari/537.36 Core/1.47.516.400 QQBrowser/9.4.8188.400");
	
	@Override
	public Site getSite() {
		if(count==0) {
		URL_LIST="/"+redisCache.getString("URL").split("lianjia.com/")[1]+"/pg\\d{1}/";
		URL_POST=redisCache.getString("URL").split("lianjia.com/")[0]+"lianjia.com/"+redisCache.getString("URL").split("lianjia.com/")[1].split("/")[0]+"/\\d+.html";
		URL_DETAIL=URL_POST.replace(".", "\\.");
		//System.out.println(redisCache.getString("URL")+"12");
	/*	System.out.println(URL_LIST);
		System.out.println(URL_DETAIL);
		System.out.println("count:"+count);*/
		}
		count++;
		return site.setSleepTime(Integer.valueOf(threadSleepTime));
	}
	public String get(Page page, String xpath) {
		if (null == xpath || xpath.equals("")) {
			return "";
		}
		String s = page.getHtml().xpath(xpath).all().toString().replace("[", "").replace("]", "").replace("\n", "")
				.replace("\r", "");
		return s;

	}
	@Override
	public void process(Page page) {
		page.addTargetRequests(page.getHtml().links().regex(URL_LIST).all());
		if (page.getUrl().regex(URL_LIST).match()) {
			//System.out.println(page.getHtml().links().regex(URL_DETAIL).all().toString());
			page.addTargetRequests(page.getHtml().links().regex(URL_DETAIL).all());
		} else if(page.getUrl().regex(URL_DETAIL).match()){
		try {
			LianJiaDO lianJiaDO = new LianJiaDO();
			lianJiaDO.setCreateTime(DateUtil.formatTo_yyyyMMdd());
			lianJiaDO.setUrl(page.getRequest().getUrl());
			
			int i1 = lianJiaDao.insert(lianJiaDO);
			if(i1<1) {
			insertFailInfo(page.getUrl().toString(), "insert PageUrl error ");
			logger.info("insert PageUrl error ");
			}
			lianJiaDO.setName(get(page, LianJiaCostant.LIANJIA_NAME));
			lianJiaDO.setAddress(get(page, LianJiaCostant.LIANJIA_ADDRESS));
			lianJiaDO.setDirType(get(page, LianJiaCostant.LIANJIA_DIRTYPR));
			lianJiaDO.setFloor(get(page, LianJiaCostant.LIANJIA_FLOOR));
			lianJiaDO.setSize(get(page, LianJiaCostant.LIANJIA_SIZE));
			lianJiaDO.setBuildTime(get(page, LianJiaCostant.LIANJIA_BUILDTIME));
			lianJiaDO.setUnitPrice(get(page, LianJiaCostant.LIANJIA_UNITPRICE));
			lianJiaDO.setTotalPrice(get(page, LianJiaCostant.LIANJIA_TOTALPRICE));
			lianJiaDO.setRoom(get(page, LianJiaCostant.LIANJIA_ROOM));
			lianJiaDO.setSpan(get(page, LianJiaCostant.LIANJIA_SPAN));
			if (page.getRequest().getUrl().contains("ershoufang")) {
				lianJiaDO.setType("二手房");
			} else {
				lianJiaDO.setType(get(page, "新房"));
			}
			lianJiaDO.setBase(get(page, LianJiaCostant.LIANJIA_BASE));
			lianJiaDO.setTradeBase(get(page, LianJiaCostant.LIANJIA_TRADEBASE));
			lianJiaDO.setFirstArea(get(page, LianJiaCostant.LIANJIA_FIRSTAREA).replace("二手房", ""));
			lianJiaDO.setSecondArea(get(page, LianJiaCostant.LIANJIA_SETSECONDAREA).replace("二手房", ""));
			lianJiaDO.setHouseType(get(page, LianJiaCostant.LIANJIA_HOUSETYPE));
			lianJiaDO.setUpdateTime(get(page, LianJiaCostant.LIANJIA_UPDATETIME));
			int i2 = lianJiaDao.update(lianJiaDO);
			if(i2<1) {
			insertFailInfo(page.getUrl().toString(), "update PageUrl error ");
			logger.info("insert PageUrl error ");
			}
		} catch (Exception e) {
			insertFailInfo(page.getUrl().toString(), "download PageUrl error ");
			logger.info("download PageUrl error ");
		}
		}
	}
	public int insertFailInfo(String url, String failInfo) {
		FailInfoDo failInfoDo = new FailInfoDo();
		failInfoDo.setUrl(url);
		failInfoDo.setCreateTime(DateUtil.formatTo_yyyyMMdd());
		failInfoDo.setFailInfo(failInfo);
		return failInfoDao.insert(failInfoDo);
	}
	
}

