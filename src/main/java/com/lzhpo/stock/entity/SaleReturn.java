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
 * 退货表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_sale_return")
public class SaleReturn extends DataEntity<SaleReturn> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户Id
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客户通知单号
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 系统通知单号
     */
    @TableField("system_code")
    private String systemCode;

    /**
     * 系统出库单号
     */
    @TableField("takeout_code")
    private String takeoutCode;

    /**
     * 异常id
     */
    @TableField("handle_abnormity_id")
    private String handleAbnormityId;

    /**
     * 退货地址
     */
    @TableField("address_id")
    private String addressId;

    /**
     * 退货类型
     */
    @TableField("return_type")
    private Integer returnType;

    /**
     * 修改状态(1待确认2可撤销3锁定4报废）
     */
    private Integer status;

    /**
     * 退货时间
     */
    @TableField("return_time")
    private LocalDate returnTime;

    /**
     * 司机Id
     */
    @TableField("driver_id")
    private String driverId;

    /**
     * 车牌
     */
    private String trucks;

    /**
     * 总数量(整)
     */
    private Integer total;

    /**
     * 退货总量(零)
     */
    private Integer number;

    /**
     * 材积
     */
    private BigDecimal volume;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 件/托
     */
    private String tray;

    private BigDecimal retrueMoney;

    /**
     * 货值
     */
    private BigDecimal money;

    /**
     * 打印次数
     */
    @TableField("print_num")
    private Integer printNum;
    
    /**
     * 
     */
    @TableField("income_id")
    private String incomeId;
    
    @TableField(exist = false)
    private String statusStr;

    @TableField(exist = false)
    private Set<SaleReturnDetail> detailSet;
    
    /**
     * 客户名称
     */
    @TableField(exist = false)
    private String clientName;
    
    

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getIncomeId() {
		return incomeId;
	}

	public void setIncomeId(String incomeId) {
		this.incomeId = incomeId;
	}

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public Set<SaleReturnDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<SaleReturnDetail> detailSet) {
		this.detailSet = detailSet;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public String getTakeoutCode() {
		return takeoutCode;
	}

	public void setTakeoutCode(String takeoutCode) {
		this.takeoutCode = takeoutCode;
	}

	public String getHandleAbnormityId() {
		return handleAbnormityId;
	}

	public void setHandleAbnormityId(String handleAbnormityId) {
		this.handleAbnormityId = handleAbnormityId;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public Integer getReturnType() {
		return returnType;
	}

	public void setReturnType(Integer returnType) {
		this.returnType = returnType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public LocalDate getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(LocalDate returnTime) {
		this.returnTime = returnTime;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getTrucks() {
		return trucks;
	}

	public void setTrucks(String trucks) {
		this.trucks = trucks;
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

	public String getTray() {
		return tray;
	}

	public void setTray(String tray) {
		this.tray = tray;
	}

	public BigDecimal getRetrueMoney() {
		return retrueMoney;
	}

	public void setRetrueMoney(BigDecimal retrueMoney) {
		this.retrueMoney = retrueMoney;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}
 
}
