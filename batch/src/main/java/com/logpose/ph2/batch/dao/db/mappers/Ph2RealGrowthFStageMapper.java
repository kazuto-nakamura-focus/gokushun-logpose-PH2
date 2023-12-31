package com.logpose.ph2.batch.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.batch.dao.db.entity.Ph2RealGrowthFStageEntity;
import com.logpose.ph2.batch.dao.db.entity.Ph2RealGrowthFStageEntityExample;
@Mapper
public interface Ph2RealGrowthFStageMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	long countByExample(Ph2RealGrowthFStageEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByExample(Ph2RealGrowthFStageEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insert(Ph2RealGrowthFStageEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int insertSelective(Ph2RealGrowthFStageEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	List<Ph2RealGrowthFStageEntity> selectByExample(
			Ph2RealGrowthFStageEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	Ph2RealGrowthFStageEntity selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExampleSelective(@Param("row") Ph2RealGrowthFStageEntity row,
			@Param("example") Ph2RealGrowthFStageEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByExample(@Param("row") Ph2RealGrowthFStageEntity row,
			@Param("example") Ph2RealGrowthFStageEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKeySelective(Ph2RealGrowthFStageEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_growth_f_stage
	 * @mbg.generated  Thu Aug 03 01:50:05 JST 2023
	 */
	int updateByPrimaryKey(Ph2RealGrowthFStageEntity row);
}