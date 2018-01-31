package com.hp.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;



@Mapper
public interface DownDao {
//	 @Insert("insert into a values(#{name,jdbcType=VARCHAR})")
	 public int insert( String name);
}
