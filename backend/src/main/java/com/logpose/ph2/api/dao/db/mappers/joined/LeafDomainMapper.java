package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LeafDomainMapper
	{
	List<Integer> getSproutDay(@Param("stage") Integer stage, 
			@Param("deviceId") Long deviceId, @Param("year") short year);
	
	}
