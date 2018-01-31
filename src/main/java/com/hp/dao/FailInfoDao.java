package com.hp.dao;


import org.apache.ibatis.annotations.Mapper;

import com.hp.dao.model.FailInfoDo;
import com.hp.dao.model.LianJiaDO;



@Mapper
public interface FailInfoDao {
	 public int insert(FailInfoDo failInfoDo);
	 
}
