package com.logpose.ph2.batch.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.batch.dao.db.entity.Ph2DevicesEnyity;
import com.logpose.ph2.batch.dao.db.entity.Ph2DevicesEnyityExample;
import com.logpose.ph2.batch.dto.ModelRefDataDTO;
@Mapper
public interface Ph2DevicesMapper {
    /**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	long countByExample(Ph2DevicesEnyityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int deleteByExample(Ph2DevicesEnyityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int insert(Ph2DevicesEnyity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int insertSelective(Ph2DevicesEnyity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	List<Ph2DevicesEnyity> selectByExample(Ph2DevicesEnyityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	Ph2DevicesEnyity selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int updateByExampleSelective(@Param("row") Ph2DevicesEnyity row,
			@Param("example") Ph2DevicesEnyityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int updateByExample(@Param("row") Ph2DevicesEnyity row,
			@Param("example") Ph2DevicesEnyityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int updateByPrimaryKeySelective(Ph2DevicesEnyity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_devices
	 * @mbg.generated  Tue Oct 10 16:20:41 JST 2023
	 */
	int updateByPrimaryKey(Ph2DevicesEnyity row);

	List<ModelRefDataDTO> selectDeviceList();

	List<Ph2DevicesEnyity> selectAll();
}