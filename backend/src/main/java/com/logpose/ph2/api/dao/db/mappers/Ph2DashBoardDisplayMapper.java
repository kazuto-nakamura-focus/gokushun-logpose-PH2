package com.logpose.ph2.api.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2DashBoardDisplayEntityExample;

@Mapper
public interface Ph2DashBoardDisplayMapper {

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  long countByExample(Ph2DashBoardDisplayEntityExample example);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int deleteByExample(Ph2DashBoardDisplayEntityExample example);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int deleteByPrimaryKey(Long deviceId);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int insert(Ph2DashBoardDisplayEntity row);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int insertSelective(Ph2DashBoardDisplayEntity row);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  List<Ph2DashBoardDisplayEntity> selectByExample(Ph2DashBoardDisplayEntityExample example);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  Ph2DashBoardDisplayEntity selectByPrimaryKey(Long deviceId);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int updateByExampleSelective(@Param("row") Ph2DashBoardDisplayEntity row,
      @Param("example") Ph2DashBoardDisplayEntityExample example);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int updateByExample(@Param("row") Ph2DashBoardDisplayEntity row,
      @Param("example") Ph2DashBoardDisplayEntityExample example);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int updateByPrimaryKeySelective(Ph2DashBoardDisplayEntity row);

  /**
   * This method was generated by MyBatis Generator. This method corresponds to the database table ph2_dashboard_display
   * @mbg.generated
   */
  int updateByPrimaryKey(Ph2DashBoardDisplayEntity row);
}