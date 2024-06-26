package com.logpose.ph2.api.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2RealPsAmountEntityExample;
import java.util.Date;
@Mapper
public interface Ph2RealPsAmountMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	long countByExample(Ph2RealPsAmountEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int deleteByExample(Ph2RealPsAmountEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int deleteByPrimaryKey(@Param("deviceId") Long deviceId, @Param("date") Date date);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int insert(Ph2RealPsAmountEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int insertSelective(Ph2RealPsAmountEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	List<Ph2RealPsAmountEntity> selectByExample(Ph2RealPsAmountEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	Ph2RealPsAmountEntity selectByPrimaryKey(@Param("deviceId") Long deviceId, @Param("date") Date date);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int updateByExampleSelective(@Param("row") Ph2RealPsAmountEntity row,
			@Param("example") Ph2RealPsAmountEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int updateByExample(@Param("row") Ph2RealPsAmountEntity row,
			@Param("example") Ph2RealPsAmountEntityExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int updateByPrimaryKeySelective(Ph2RealPsAmountEntity row);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_real_ps_amount
	 * @mbg.generated
	 */
	int updateByPrimaryKey(Ph2RealPsAmountEntity row);
}