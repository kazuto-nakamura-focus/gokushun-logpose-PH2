package com.logpose.ph2.batch.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.batch.dao.db.entity.SensorsEntity;
import com.logpose.ph2.batch.dao.db.entity.SensorsEntityExample;
@Mapper
public interface SensorsMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	long countByExample(SensorsEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByExample(SensorsEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insert(SensorsEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insertSelective(SensorsEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	List<SensorsEntity> selectByExample(SensorsEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	SensorsEntity selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExampleSelective(@Param("row") SensorsEntity row,
			@Param("example") SensorsEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExample(@Param("row") SensorsEntity row,
			@Param("example") SensorsEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKeySelective(SensorsEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_sensors
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKey(SensorsEntity row);
}