package com.lzhpo.warehouse.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 盘点表
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_inventory")
public class Inventory extends DataEntity<Inventory> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户id
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 盘点类型(0:全盘; 1:循环; 2:异动3;客户盘点)
     */
    @TableField("auditor_type")
    private Integer auditorType;

    /**
     * 盘点员
     */
    private String auditor;

    /**
     * 开始时间
     */
    @TableField("inventory_time")
    private LocalDate inventoryTime;

    /**
     * 盘点结果(1正常，2差异);
     */
    private Integer status;

    /**
     * 异常盘点开始时间
     */
    @TableField("start_time")
    private LocalDate startTime;

    /**
     * 异常盘点结束时间
     */
    @TableField("end_time")
    private LocalDate endTime;

    /**
     * 盘点状态(0:未盘点; 1:已盘点)
     */
    @TableField("inventory_status")
    private Integer inventoryStatus;

    /**
     * 循环批次(1:第一天; 2:第二天; 3:第三天)
     */
    @TableField("cycle_batch")
    private Integer cycleBatch;

    /**
     * 是否忽略批次(1是2否)
     */
    @TableField("batch_status")
    private Integer batchStatus;

    
    @TableField(exist = false)
    private String auditorTypeStr;// 
    
    @TableField(exist = false)
    private String typeStr;
    
    @TableField(exist = false)
    private String clientName;
    
    
    @TableField(exist = false)
    private String inventoryStatusStr;

    @TableField(exist = false)
    private String statusStr;// 
    
	public String getInventoryStatusStr() {
		return inventoryStatusStr;
	}

	public void setInventoryStatusStr(String inventoryStatusStr) {
		this.inventoryStatusStr = inventoryStatusStr;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}



	public String getAuditorTypeStr() {
		return auditorTypeStr;
	}

	public void setAuditorTypeStr(String auditorTypeStr) {
		this.auditorTypeStr = auditorTypeStr;
	}

	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public Integer getAuditorType() {
		return auditorType;
	}

	public void setAuditorType(Integer auditorType) {
		this.auditorType = auditorType;
	}

	public String getAuditor() {
		return auditor;
	}

	public void setAuditor(String auditor) {
		this.auditor = auditor;
	}

	public LocalDate getInventoryTime() {
		return inventoryTime;
	}

	public void setInventoryTime(LocalDate inventoryTime) {
		this.inventoryTime = inventoryTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDate getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDate startTime) {
		this.startTime = startTime;
	}

	public LocalDate getEndTime() {
		return endTime;
	}

	public void setEndTime(LocalDate endTime) {
		this.endTime = endTime;
	}

	public Integer getInventoryStatus() {
		return inventoryStatus;
	}

	public void setInventoryStatus(Integer inventoryStatus) {
		this.inventoryStatus = inventoryStatus;
	}

	public Integer getCycleBatch() {
		return cycleBatch;
	}

	public void setCycleBatch(Integer cycleBatch) {
		this.cycleBatch = cycleBatch;
	}

	public Integer getBatchStatus() {
		return batchStatus;
	}

	public void setBatchStatus(Integer batchStatus) {
		this.batchStatus = batchStatus;
	}

    
}
