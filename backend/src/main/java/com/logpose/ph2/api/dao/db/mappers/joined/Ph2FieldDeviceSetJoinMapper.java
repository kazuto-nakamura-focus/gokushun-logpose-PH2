package com.logpose.ph2.api.dao.db.mappers.joined;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dto.DeviceInfoDTO;
import com.logpose.ph2.api.dto.device.DeviceDetailDTO;
import com.logpose.ph2.api.dto.element.DeviceInfo;


@Mapper
public interface Ph2FieldDeviceSetJoinMapper
	{
    List<DeviceInfo> selectDeviceInfoByField(@Param("fieldId") Long fieldId);
    
    List<DeviceInfoDTO> selectDeviceList();
    
    DeviceDetailDTO selectDeviceDetail(@Param("deviceId") Long deviceId);
	}
