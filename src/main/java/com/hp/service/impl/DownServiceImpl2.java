package com.hp.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;

import com.hp.dao.DownDao;
import com.hp.dao.TestDao;
import com.hp.dao.model.TestDO;
import com.hp.service.DownService;
import com.hp.service.DownService2;
import com.hp.service.TestService;

import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.Pipeline;





@Service
public class DownServiceImpl2 implements DownService2{
	private static Logger logger = LoggerFactory.getLogger(DownServiceImpl2.class);
	
	
	@Autowired
	private DownDao downDao;
	
	
	@Override
	public void down(String name) {
		downDao.insert(name);
	}
	

}
