package com.lzhpo.deliver.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2020-09-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_dispatch_receipt_bill")
public class DispatchReceiptBill extends DataEntity<DispatchReceiptBill> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关联id 表deliver_dispact_adress
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
    @TableField("receipt_time")
    private LocalDateTime receiptTime;

    /**
     * 异常原因类型
     */
    @TableField("abnormity_type")
    private Integer abnormityType;

    /**
     * 配送回单时间
     */
    @TableField("delivery_receipt_time")
    private LocalDateTime deliveryReceiptTime;

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

	public Integer getAbnormityType() {
		return abnormityType;
	}

	public void setAbnormityType(Integer abnormityType) {
		this.abnormityType = abnormityType;
	}

	public LocalDateTime getDeliveryReceiptTime() {
		return deliveryReceiptTime;
	}

	public void setDeliveryReceiptTime(LocalDateTime deliveryReceiptTime) {
		this.deliveryReceiptTime = deliveryReceiptTime;
	}

    


}
