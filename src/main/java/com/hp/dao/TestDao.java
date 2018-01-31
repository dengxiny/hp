package com.hp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.hp.dao.model.TestDO;


@Mapper
public interface TestDao {
	public List<String> update();
}
