package com.lzhpo.customer.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 异常日报
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_service_excptionreport")
public class ServiceExcptionreport extends DataEntity<ServiceExcptionreport> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("client_id")
    private String clientId;

    @TableField("client_name")
    private String clientName;

    /**
     * 出库时间
     */
    @TableField("takeout_time")
    private LocalDate takeoutTime;

    /**
     * 送达方id
     */
    @TableField("address_id")
    private String addressId;

    @TableField("address_name")
    private String addressName;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("time_start")
    private LocalDateTime timeStart;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("time_end")
    private LocalDateTime timeEnd;

    /**
     * 反馈人
     */
    @TableField("feedback_people")
    private String feedbackPeople;

    /**
     * 处理结果
     */
    @TableField("handle_result")
    private String handleResult;

    @TableField("excption_id")
    private String excptionId;

    @TableField("excption_name")
    private String excptionName;

    @TableField("share_people_id")
    private String sharePeopleId;

    private Integer responsibility;

    /**
     * 责任部门
     */
    @TableField("responsibility_dept")
    private String responsibilityDept;

    @TableField("abnormity_type_id")
    private String abnormityTypeId;

    @TableField("abnormity_type_name")
    private String abnormityTypeName;
    /**
     * 责任部门名称
     */
    @TableField("responsibility_dept_name")
    private String responsibilityDeptName;


	public String getAbnormityTypeId() {
		return abnormityTypeId;
	}


	public void setAbnormityTypeId(String abnormityTypeId) {
		this.abnormityTypeId = abnormityTypeId;
	}


	public String getAbnormityTypeName() {
		return abnormityTypeName;
	}


	public void setAbnormityTypeName(String abnormityTypeName) {
		this.abnormityTypeName = abnormityTypeName;
	}


	public String getClientId() {
		return clientId;
	}


	public void setClientId(String clientId) {
		this.clientId = clientId;
	}


	public String getClientName() {
		return clientName;
	}


	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public LocalDate getTakeoutTime() {
		return takeoutTime;
	}


	public void setTakeoutTime(LocalDate takeoutTime) {
		this.takeoutTime = takeoutTime;
	}


	public String getAddressId() {
		return addressId;
	}


	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}


	public String getAddressName() {
		return addressName;
	}


	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}


	public LocalDateTime getTimeStart() {
		return timeStart;
	}


	public void setTimeStart(LocalDateTime timeStart) {
		this.timeStart = timeStart;
	}


	public LocalDateTime getTimeEnd() {
		return timeEnd;
	}


	public void setTimeEnd(LocalDateTime timeEnd) {
		this.timeEnd = timeEnd;
	}


	public String getFeedbackPeople() {
		return feedbackPeople;
	}


	public void setFeedbackPeople(String feedbackPeople) {
		this.feedbackPeople = feedbackPeople;
	}


	public String getHandleResult() {
		return handleResult;
	}


	public void setHandleResult(String handleResult) {
		this.handleResult = handleResult;
	}


	public String getExcptionId() {
		return excptionId;
	}


	public void setExcptionId(String excptionId) {
		this.excptionId = excptionId;
	}


	public String getExcptionName() {
		return excptionName;
	}


	public void setExcptionName(String excptionName) {
		this.excptionName = excptionName;
	}


	public String getSharePeopleId() {
		return sharePeopleId;
	}


	public void setSharePeopleId(String sharePeopleId) {
		this.sharePeopleId = sharePeopleId;
	}


	public Integer getResponsibility() {
		return responsibility;
	}


	public void setResponsibility(Integer responsibility) {
		this.responsibility = responsibility;
	}


	public String getResponsibilityDept() {
		return responsibilityDept;
	}


	public void setResponsibilityDept(String responsibilityDept) {
		this.responsibilityDept = responsibilityDept;
	}


	public String getResponsibilityDeptName() {
		return responsibilityDeptName;
	}


	public void setResponsibilityDeptName(String responsibilityDeptName) {
		this.responsibilityDeptName = responsibilityDeptName;
	}

    

}
