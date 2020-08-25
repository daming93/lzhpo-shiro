package com.lzhpo.deliver.entity;

import java.math.BigDecimal;
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
 * @since 2020-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_vehicle_type")
public class VehicleType extends DataEntity<VehicleType> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车型名称
     */
    private String name;

    /**
     * 合同id
     */
    @TableField("contract_id")
    private String contractId;

    /**
     * 容积
     */
    private BigDecimal volume;

    /**
     * 承重
     */
    private BigDecimal bearing;

    
    @TableField(exist=false)
    private String contractName;
    
    
	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

	public BigDecimal getVolume() {
		return volume;
	}

	public void setVolume(BigDecimal volume) {
		this.volume = volume;
	}

	public BigDecimal getBearing() {
		return bearing;
	}

	public void setBearing(BigDecimal bearing) {
		this.bearing = bearing;
	}
}
