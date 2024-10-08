package com.logpose.ph2.api.dao.db.entity;

import java.util.Date;

public class Ph2BatchLogEntity {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ph2_batch_log.device_id
     *
     * @mbg.generated
     */
    private Long deviceId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ph2_batch_log.upload_begin_time
     *
     * @mbg.generated
     */
    private Date uploadBeginTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ph2_batch_log.upload_end_time
     *
     * @mbg.generated
     */
    private Date uploadEndTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ph2_batch_log.update_begin_time
     *
     * @mbg.generated
     */
    private Date updateBeginTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column ph2_batch_log.update_end_time
     *
     * @mbg.generated
     */
    private Date updateEndTime;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ph2_batch_log.device_id
     *
     * @return the value of ph2_batch_log.device_id
     *
     * @mbg.generated
     */
    public Long getDeviceId() {
        return deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ph2_batch_log.device_id
     *
     * @param deviceId the value for ph2_batch_log.device_id
     *
     * @mbg.generated
     */
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ph2_batch_log.upload_begin_time
     *
     * @return the value of ph2_batch_log.upload_begin_time
     *
     * @mbg.generated
     */
    public Date getUploadBeginTime() {
        return uploadBeginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ph2_batch_log.upload_begin_time
     *
     * @param uploadBeginTime the value for ph2_batch_log.upload_begin_time
     *
     * @mbg.generated
     */
    public void setUploadBeginTime(Date uploadBeginTime) {
        this.uploadBeginTime = uploadBeginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ph2_batch_log.upload_end_time
     *
     * @return the value of ph2_batch_log.upload_end_time
     *
     * @mbg.generated
     */
    public Date getUploadEndTime() {
        return uploadEndTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ph2_batch_log.upload_end_time
     *
     * @param uploadEndTime the value for ph2_batch_log.upload_end_time
     *
     * @mbg.generated
     */
    public void setUploadEndTime(Date uploadEndTime) {
        this.uploadEndTime = uploadEndTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ph2_batch_log.update_begin_time
     *
     * @return the value of ph2_batch_log.update_begin_time
     *
     * @mbg.generated
     */
    public Date getUpdateBeginTime() {
        return updateBeginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ph2_batch_log.update_begin_time
     *
     * @param updateBeginTime the value for ph2_batch_log.update_begin_time
     *
     * @mbg.generated
     */
    public void setUpdateBeginTime(Date updateBeginTime) {
        this.updateBeginTime = updateBeginTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column ph2_batch_log.update_end_time
     *
     * @return the value of ph2_batch_log.update_end_time
     *
     * @mbg.generated
     */
    public Date getUpdateEndTime() {
        return updateEndTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column ph2_batch_log.update_end_time
     *
     * @param updateEndTime the value for ph2_batch_log.update_end_time
     *
     * @mbg.generated
     */
    public void setUpdateEndTime(Date updateEndTime) {
        this.updateEndTime = updateEndTime;
    }
}