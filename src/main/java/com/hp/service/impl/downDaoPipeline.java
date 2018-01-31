package com.hp.service.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.hp.HpApplication;
import com.hp.dao.DownDao;
import com.hp.dao.model.DownDO;
import com.hp.service.DownService2;

import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.PageModelPipeline;
import us.codecraft.webmagic.pipeline.Pipeline;

@Component("downDaoPipeline")
@SpringBootApplication  
public class downDaoPipeline implements Pipeline {

    @Autowired
    public DownService2 downService2;

  /*  @Override
    public void process(DownDO downDO, Task task) {
        //调用MyBatis DAO保存结果
    	System.out.println(1);
    	downDao.insert(downDO.getName());
    }*/

	@Override
	public  void process(ResultItems resultItems, Task task) {
		DownDO downDO = (DownDO)resultItems.get("name");
		/*System.out.println(downDO.getName());
		ApplicationContext ac = new ClassPathXmlApplicationContext(
				"classpath:application.yml");
		DownService2 downService2=(DownService2) ac.getBean(DownService2.class);*/
		//DownService2 downService2=(DownService2) new SpringApplication(DownService2.class);
		downService2.down(downDO.getName());
    	/*downDao.insert(downDO.getName());*/
		
	}

	
}