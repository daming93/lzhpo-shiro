package com.lzhpo.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 
 * </p>
 *
 * @author xdm
 * @since 2020-11-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_receipt_bill")
public class ReceiptBill extends DataEntity<ReceiptBill> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联id 表takeout 出库得时候就有单据了，确认出库得时候有，撤销得时候删除
     */
    @TableField("ref_id")
    private String refId;

    /**
     * 是否有送货单(0:是;  1:否)
     */
    @TableField("is_exist_slip")
    private Integer isExistSlip;

    /**
     * 是否有验收单(0:是;  1:否)
     */
    @TableField("is_exist_receipt")
    private Integer isExistReceipt;

    /**
     * 是否有退单(0:是;  1:否)
     */
    @TableField("is_exist_back")
    private Integer isExistBack;

    /**
     * 回单状态(0:待录单;1:待确认; 2:待审核; 3:已确认)
     */
    @TableField("receipt_status")
    private Integer receiptStatus;

    /**
     * 回单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("receipt_time")
    private LocalDateTime receiptTime;

    /**
     * 异常类型
     */
    @TableField("abnormity_type_id")
    private String abnormityTypeId;

    /**
     * 异常原因Id
     */
    @TableField("abnormity_id")
    private String abnormityId;

    /**
     * 配送回单时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("delivery_receipt_time")
    private LocalDateTime deliveryReceiptTime;

    @TableField(exist=false)
    private Takeout takeout;
    
    @TableField(exist=false)
    private String abnormityTypeName;
    
    @TableField(exist=false)
    private String abnormityName;
    
    @TableField(exist=false)
    private String receiptStatusName;
    
    @TableField(exist=false)
    private Integer type;//编辑类型 1 是首审  2是终审，给不同得时间
    
    
    
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getReceiptStatusName() {
		return receiptStatusName;
	}

	public void setReceiptStatusName(String receiptStatusName) {
		this.receiptStatusName = receiptStatusName;
	}

	public String getAbnormityTypeName() {
		return abnormityTypeName;
	}

	public void setAbnormityTypeName(String abnormityTypeName) {
		this.abnormityTypeName = abnormityTypeName;
	}

	public String getAbnormityName() {
		return abnormityName;
	}

	public void setAbnormityName(String abnormityName) {
		this.abnormityName = abnormityName;
	}

	public Takeout getTakeout() {
		return takeout;
	}

	public void setTakeout(Takeout takeout) {
		this.takeout = takeout;
	}

	public String getRefId() {
		return refId;
	}

	public void setRefId(String refId) {
		this.refId = refId;
	}

	public Integer getIsExistSlip() {
		return isExistSlip;
	}

	public void setIsExistSlip(Integer isExistSlip) {
		this.isExistSlip = isExistSlip;
	}

	public Integer getIsExistReceipt() {
		return isExistReceipt;
	}

	public void setIsExistReceipt(Integer isExistReceipt) {
		this.isExistReceipt = isExistReceipt;
	}

	public Integer getIsExistBack() {
		return isExistBack;
	}

	public void setIsExistBack(Integer isExistBack) {
		this.isExistBack = isExistBack;
	}

	public Integer getReceiptStatus() {
		return receiptStatus;
	}

	public void setReceiptStatus(Integer receiptStatus) {
		this.receiptStatus = receiptStatus;
	}

	public LocalDateTime getReceiptTime() {
		return receiptTime;
	}

	public void setReceiptTime(LocalDateTime receiptTime) {
		this.receiptTime = receiptTime;
	}

	public String getAbnormityTypeId() {
		return abnormityTypeId;
	}

	public void setAbnormityTypeId(String abnormityTypeId) {
		this.abnormityTypeId = abnormityTypeId;
	}

	public String getAbnormityId() {
		return abnormityId;
	}

	public void setAbnormityId(String abnormityId) {
		this.abnormityId = abnormityId;
	}

	public LocalDateTime getDeliveryReceiptTime() {
		return deliveryReceiptTime;
	}

	public void setDeliveryReceiptTime(LocalDateTime deliveryReceiptTime) {
		this.deliveryReceiptTime = deliveryReceiptTime;
	}



}
