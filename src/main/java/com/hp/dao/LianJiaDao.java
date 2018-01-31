package com.hp.dao;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hp.dao.model.LianJiaDO;



@Mapper
public interface LianJiaDao {
//	 @Insert("insert into a values(#{name,jdbcType=VARCHAR})")
	 public int insert(LianJiaDO lianJiaDO);
	 
	 public int update(LianJiaDO lianJiaDO);

	 public List<String> selectUrllist(LianJiaDO lianJiaDO);

	 public List<String> selectErrorUrlList(LianJiaDO lianJiaDO);
}
