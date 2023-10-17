package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dto.HistoryDTO;
import com.logpose.ph2.api.dto.ParamSetDTO;
import com.logpose.ph2.api.dto.ParamSetExtendDTO;

@Mapper
public interface Ph2ParamSetJoinMapper
	{
	List<ParamSetDTO> listParamerSet(@Param("modelId") Integer modelId);
	
	List<ParamSetExtendDTO> listSimpleParamerSet(@Param("modelId") Integer modelId);
	
    List<HistoryDTO> selectHistory(@Param("paramsetId") Long paramsetId);
    
    HistoryDTO selectLatestHistory(@Param("paramsetId") Long paramsetId);
	}
