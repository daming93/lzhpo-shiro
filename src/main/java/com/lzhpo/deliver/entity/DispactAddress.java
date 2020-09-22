package com.lzhpo.deliver.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
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
@TableName("deliver_dispact_address")
public class DispactAddress extends DataEntity<DispactAddress> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    private String code;

    @TableField("dispacth_id")
    private String dispacthId;

    /**
     * 省id
     */
    @TableField("province_id")
    private String provinceId;

    /**
     * 市id
     */
    @TableField("city_id")
    private String cityId;

    /**
     * 区id
     */
    @TableField("counties_id")
    private String countiesId;

    /**
     * 地域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 类型(1 出库单来源 takeout 2 快单 express_bill )
     */
    private Integer type;

    /**
     * 表单id
     */
    @TableField("table_id")
    private String tableId;

    /**
     * 回单id
     */
    @TableField("receipt_bill_id")
    private String receiptBillId;

    /**
     * 配送进度(预计在手机端进行这个操作) 暂时先放着
     */
    @TableField("deliver_history_id")
    private String deliverHistoryId;

    /**
     * 发货总数(整)
     */
    private Integer total;

    /**
     * 材积
     */
    private BigDecimal volume;

    /**
     * 重量
     */
    private BigDecimal weight;
    
    @TableField("client_name")
    private String clientName;
    
    @TableField("client_code")
    private String clientCode;

    
    @TableField("dispatch_time")
    private LocalDate dispatchTime;
    
    @TableField(exist=false)
    private  String countiesName;
    
    @TableField(exist=false)
    private  Integer adjustmentTotal;
    
    @TableField(exist=false)
    private  BigDecimal adjustmentVolume;
    
    @TableField(exist=false)
    private  BigDecimal adjustmentWeight;
    
    @TableField(exist=false)
    private  String typeStr;
    

    
	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public Integer getAdjustmentTotal() {
		return adjustmentTotal;
	}

	public void setAdjustmentTotal(Integer adjustmentTotal) {
		this.adjustmentTotal = adjustmentTotal;
	}

	public BigDecimal getAdjustmentVolume() {
		return adjustmentVolume;
	}

	public void setAdjustmentVolume(BigDecimal adjustmentVolume) {
		this.adjustmentVolume = adjustmentVolume;
	}

	public BigDecimal getAdjustmentWeight() {
		return adjustmentWeight;
	}

	public void setAdjustmentWeight(BigDecimal adjustmentWeight) {
		this.adjustmentWeight = adjustmentWeight;
	}

	public String getCountiesName() {
		return countiesName;
	}

	public void setCountiesName(String countiesName) {
		this.countiesName = countiesName;
	}

	public LocalDate getDispatchTime() {
		return dispatchTime;
	}

	public void setDispatchTime(LocalDate dispatchTime) {
		this.dispatchTime = dispatchTime;
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

	public String getDispacthId() {
		return dispacthId;
	}

	public void setDispacthId(String dispacthId) {
		this.dispacthId = dispacthId;
	}

	public String getProvinceId() {
		return provinceId;
	}

	public void setProvinceId(String provinceId) {
		this.provinceId = provinceId;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getCountiesId() {
		return countiesId;
	}

	public void setCountiesId(String countiesId) {
		this.countiesId = countiesId;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getReceiptBillId() {
		return receiptBillId;
	}

	public void setReceiptBillId(String receiptBillId) {
		this.receiptBillId = receiptBillId;
	}

	public String getDeliverHistoryId() {
		return deliverHistoryId;
	}

	public void setDeliverHistoryId(String deliverHistoryId) {
		this.deliverHistoryId = deliverHistoryId;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
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



}
