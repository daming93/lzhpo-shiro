package com.lzhpo.client.entity;

import java.io.Serializable;
import java.math.BigDecimal;

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
 * @since 2020-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_deliver_contract_main_detail")
public class DeliverContractMainDetail extends DataEntity<DeliverContractMainDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 最小值
     */
    @TableField("min_number")
    private BigDecimal minNumber;

    /**
     * 最大值
     */
    @TableField("max_number")
    private BigDecimal maxNumber;

    /**
     * 金额
     */
    private BigDecimal money;

    /**
     * 结算类型
     */
    private Integer type;

    /**
     * 省
     */
    @TableField("province_id")
    private String provinceId;

    /**
     * 市
     */
    @TableField("city_id")
    private String cityId;

    /**
     * 区
     */
    @TableField("area_id")
    private String areaId;

    /**
     * 以重还是方结算类型(1重2方)
     */
    @TableField("consult_type")
    private Integer consultType;

    /**
     * 结算类型(0单价 1 固收）
     */
    @TableField("money_type")
    private Integer moneyType;
    
    /**
     * 合同Id(主表id)
     */
    @TableField("contract_id")
    private String contractId;

    /**
     * 收费项目id（预留）
     */
    @TableField("option_id")
    private String optionId;

    /**
     * 类型名
     */
    @TableField("type_name")
    private String typeName;
    
    /**
     * 类型名
     */
    @TableField("money_type_name")
    private String moneyTypeName;
    
    @TableField(exist=false)
    private String provinceName;
    
    @TableField(exist=false)
    private String cityName;
    
    @TableField(exist=false)
    private String areaName;
    
    
    
	public Integer getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(Integer moneyType) {
		this.moneyType = moneyType;
	}

	public String getMoneyTypeName() {
		return moneyTypeName;
	}

	public void setMoneyTypeName(String moneyTypeName) {
		this.moneyTypeName = moneyTypeName;
	}

	public String getProvinceName() {
		return provinceName;
	}

	public void setProvinceName(String provinceName) {
		this.provinceName = provinceName;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public BigDecimal getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(BigDecimal minNumber) {
		this.minNumber = minNumber;
	}

	public BigDecimal getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(BigDecimal maxNumber) {
		this.maxNumber = maxNumber;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
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

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public Integer getConsultType() {
		return consultType;
	}

	public void setConsultType(Integer consultType) {
		this.consultType = consultType;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}
}
