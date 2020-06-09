package com.lzhpo.stock.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 入库主表
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_storage")
public class Storage extends DataEntity<Storage> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户Id
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客户名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 客户通知单号
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 系统单号
     */
    private String code;

    /**
     * 修改状态（1待确认2可撤销3锁定）
     */
    private Integer status;

    /**
     * 打印次数
     */
    @TableField("print_num")
    private Integer printNum;

    /**
     * 入库时间
     */
    @TableField("storage_time")
    private LocalDate storageTime;

    /**
     * 入库总数量(整)
     */
    private Integer total;

    /**
     * 入库总数量(零)
     */
    private Integer number;

    /**
     * 实际数量
     */
    @TableField("actual_amount")
    private String actualAmount;

    /**
     * 入库总体积
     */
    private BigDecimal volume;

    /**
     * 入库总重量
     */
    private BigDecimal weight;

    /**
     * 件/托
     */
    @TableField("tray_num")
    private Integer trayNum;

    /**
     * 调账 0是正常 1是调账
     */
    private Integer adjustment;
    
    /**
     * 
     */
    @TableField("income_id")
    private String incomeId;
    
    @TableField(exist = false)
    private String statusStr;

    @TableField(exist = false)
    private Set<StorageDetail> detailSet;
    
	public Set<StorageDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<StorageDetail> detailSet) {
		this.detailSet = detailSet;
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

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public LocalDate getStorageTime() {
		return storageTime;
	}

	public void setStorageTime(LocalDate storageTime) {
		this.storageTime = storageTime;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getActualAmount() {
		return actualAmount;
	}

	public void setActualAmount(String actualAmount) {
		this.actualAmount = actualAmount;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getWeight() {
		return weight;
	}

	public void setWeight(BigDecimal weight) {
		this.weight = weight;
	}

	public Integer getTrayNum() {
		return trayNum;
	}

	public void setTrayNum(Integer trayNum) {
		this.trayNum = trayNum;
	}

	public Integer getAdjustment() {
		return adjustment;
	}

	public void setAdjustment(Integer adjustment) {
		this.adjustment = adjustment;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public String getIncomeId() {
		return incomeId;
	}

	public void setIncomeId(String incomeId) {
		this.incomeId = incomeId;
	}
}
