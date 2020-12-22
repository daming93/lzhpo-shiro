package com.lzhpo.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

import org.springframework.web.jsf.FacesContextUtils;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
import com.lzhpo.deliver.entity.WayBill;
/**
 * <p>
 * 异常处理表
 * </p>
 *
 * @author xdm
 * @since 2020-12-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_handle_abnormity")
public class HandleAbnormity extends DataEntity<HandleAbnormity> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 路单id
     */
    @TableField("way_bill_id")
    private String wayBillId;

    /**
     * 调度物料表id
     */
    @TableField("takeout_id")
    private String takeoutId;

    /**
     * 审核状态(0:未审核; 1:已审核)
     */
    @TableField("audit_status")
    private Integer auditStatus;

    /**
     * 审核人id
     */
    @TableField("audit_user_id")
    private String auditUserId;

    /**
     * 审核时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("audit_time")
    private LocalDateTime auditTime;

    /**
     * 异常处理状态(0:未处理;  1:已处理)
     */
    @TableField("dispose_status")
    private Integer disposeStatus;

    /**
     * 异常处理人id
     */
    @TableField("dispose_user_id")
    private String disposeUserId;

    /**
     * 异常处理时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("dispose_time")
    private LocalDateTime disposeTime;

    /**
     * 异常类型
     */
    @TableField("abnormity_type_id")
    private String abnormityTypeId;

    /**
     * 异常类型名称
     */
    @TableField("abnormity_type_name")
    private String abnormityTypeName;

    /**
     * 异常原因
     */
    @TableField("abnormity_id")
    private String abnormityId;

    @TableField("abnormity_name")
    private String abnormityName;

    @TableField(exist=false)
    private Takeout takeout;
    
    @TableField(exist=false)
    private WayBill wayBill;
    
    /**
     * 修改状态(1待选择，2直退，3二次配送）
     */
    private Integer status;

    
	public Takeout getTakeout() {
		return takeout;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setTakeout(Takeout takeout) {
		this.takeout = takeout;
	}

	public WayBill getWayBill() {
		return wayBill;
	}

	public void setWayBill(WayBill wayBill) {
		this.wayBill = wayBill;
	}

	public String getWayBillId() {
		return wayBillId;
	}

	public void setWayBillId(String wayBillId) {
		this.wayBillId = wayBillId;
	}

	public String getTakeoutId() {
		return takeoutId;
	}

	public void setTakeoutId(String takeoutId) {
		this.takeoutId = takeoutId;
	}

	public Integer getAuditStatus() {
		return auditStatus;
	}

	public void setAuditStatus(Integer auditStatus) {
		this.auditStatus = auditStatus;
	}


	public String getAuditUserId() {
		return auditUserId;
	}

	public void setAuditUserId(String auditUserId) {
		this.auditUserId = auditUserId;
	}

	public LocalDateTime getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(LocalDateTime auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getDisposeStatus() {
		return disposeStatus;
	}

	public void setDisposeStatus(Integer disposeStatus) {
		this.disposeStatus = disposeStatus;
	}

	
	public String getDisposeUserId() {
		return disposeUserId;
	}

	public void setDisposeUserId(String disposeUserId) {
		this.disposeUserId = disposeUserId;
	}

	public LocalDateTime getDisposeTime() {
		return disposeTime;
	}

	public void setDisposeTime(LocalDateTime disposeTime) {
		this.disposeTime = disposeTime;
	}

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

	public String getAbnormityId() {
		return abnormityId;
	}

	public void setAbnormityId(String abnormityId) {
		this.abnormityId = abnormityId;
	}

	public String getAbnormityName() {
		return abnormityName;
	}

	public void setAbnormityName(String abnormityName) {
		this.abnormityName = abnormityName;
	}


}
