package com.logpose.ph2.api.dao.db.mappers;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntity;
import com.logpose.ph2.api.dao.db.entity.Ph2SensorsEntityExample;
@Mapper
public interface Ph2SensorsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    long countByExample(Ph2SensorsEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int deleteByExample(Ph2SensorsEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    Long insert(Ph2SensorsEntity row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int insertSelective(Ph2SensorsEntity row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    List<Ph2SensorsEntity> selectByExample(Ph2SensorsEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    Ph2SensorsEntity selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int updateByExampleSelective(@Param("row") Ph2SensorsEntity row, @Param("example") Ph2SensorsEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int updateByExample(@Param("row") Ph2SensorsEntity row, @Param("example") Ph2SensorsEntityExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int updateByPrimaryKeySelective(Ph2SensorsEntity row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ph2_sensors
     *
     * @mbg.generated Tue Aug 01 03:54:09 JST 2023
     */
    int updateByPrimaryKey(Ph2SensorsEntity row);
}