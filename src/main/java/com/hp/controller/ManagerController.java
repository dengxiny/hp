package com.hp.controller;

import java.lang.reflect.Field;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hp.dao.model.IpWeight;

import us.codecraft.webmagic.Spider;

@RestController
public class ManagerController {
	private IpWeight i1;
	private IpWeight i2;
	@RequestMapping("/manage.do")
	public String manage(String uuid) {
		IpWeight i=null;
		if(uuid.equals(i1.getAddress())) {
			i=i1;
		}else if(uuid.equals(i2.getAddress())) {
			i=i2;
		}

		return i.toString();
	} 
	@RequestMapping("/managelist.do")
	public String manage() {
		System.out.println(i1);
		System.out.println(i2);
		return "1";
	}
	@RequestMapping("/manage/test1.do")
	public String managedo() {
/*		Spider sp=Spider.create(pageProcessor)
				sp.setUUID("2");
		sp.run();*/
		i1=new IpWeight("a");
		return "1";
	} 
	@RequestMapping("/manage/test2.do")
	public String managedo2() {
/*		Spider sp=Spider.create(pageProcessor)
				sp.setUUID("1");
		sp.run();*/
		i2=new IpWeight("b");
		return "1";
	} 
}
