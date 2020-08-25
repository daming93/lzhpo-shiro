package com.lzhpo.deliver.entity;

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
 * 合同收费项
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_vehicle_contract_main_detail")
public class VehicleContractMainDetail extends DataEntity<VehicleContractMainDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 合同Id(主表id)
     */
    @TableField("contract_id")
    private String contractId;
    
    @TableField("min_number")
    private Integer minNumber;
    
    @TableField("max_number")
    private Integer maxNumber;

    /**
     * 收费项目id（预留）
     */
    @TableField("option_id")
    private String optionId;
    
    @TableField(exist=false)
    private String provinceName;
    
    @TableField(exist=false)
    private String cityName;
    
    @TableField(exist=false)
    private String areaName;
    
    
    
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

	public Integer getMinNumber() {
		return minNumber;
	}

	public void setMinNumber(Integer minNumber) {
		this.minNumber = minNumber;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
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

   

}
