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
 * 车辆信息表
 * </p>
 *
 * @author xdm
 * @since 2020-08-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_vehicle")
public class Vehicle extends DataEntity<Vehicle> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 车牌号
     */
    @TableField("licence_plate")
    private String licencePlate;

    /**
     * 车辆类型
     */
    @TableField("vehicle_type_id")
    private String vehicleTypeId;

    /**
     * 合同类型(0:自有车; 1:合同车; 2:临时车)
     */
    @TableField("pact_type")
    private Integer pactType;

    /**
     * 驾驶员id
     */
    @TableField("driver_id")
    private String driverId;

    /**
     * 保险单号
     */
    @TableField("insurance_num")
    private String insuranceNum;

    /**
     * 保险到期时间
     */
    @TableField("vehicle_expiration_time")
    private LocalDate vehicleExpirationTime;

    /**
     * 年审到期时间
     */
    @TableField("audit_time")
    private LocalDate auditTime;

    /**
     * 车辆属性(0:厢车; 1:平板; 2:高栏; 3:面包)
     */
    @TableField("vehicle_nature")
    private Integer vehicleNature;

    /**
     * 车辆排车状态(0:未分配; 1:已分配)
     */
    @TableField("vehicle_line_status")
    private Integer vehicleLineStatus;

    /**
     * 车辆归属
     */
    @TableField("vehicle_belong")
    private Integer vehicleBelong;

    /**
     * 车辆状态(0:正常; 1:条件不符)
     */
    @TableField("vehicle_status")
    private Integer vehicleStatus;

    @TableField(exist=false)
    private String typeName;
    
    @TableField(exist=false)
    private String driverName;
    
    @TableField(exist=false)
    private String vehicleStatusStr;
    
    /**
     * 容积
     */
    @TableField(exist=false)
    private BigDecimal volume;

    /**
     * 承重
     */
    @TableField(exist=false)
    private BigDecimal bearing;
    
    
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

	public String getVehicleStatusStr() {
		return vehicleStatusStr;
	}

	public void setVehicleStatusStr(String vehicleStatusStr) {
		this.vehicleStatusStr = vehicleStatusStr;
	}

	public String getDriverName() {
		return driverName;
	}

	public void setDriverName(String driverName) {
		this.driverName = driverName;
	}

	public String getLicencePlate() {
		return licencePlate;
	}

	public void setLicencePlate(String licencePlate) {
		this.licencePlate = licencePlate;
	}

	public String getVehicleTypeId() {
		return vehicleTypeId;
	}

	public void setVehicleTypeId(String vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}

	public Integer getPactType() {
		return pactType;
	}

	public void setPactType(Integer pactType) {
		this.pactType = pactType;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	public String getInsuranceNum() {
		return insuranceNum;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public void setInsuranceNum(String insuranceNum) {
		this.insuranceNum = insuranceNum;
	}

	public LocalDate getVehicleExpirationTime() {
		return vehicleExpirationTime;
	}

	public void setVehicleExpirationTime(LocalDate vehicleExpirationTime) {
		this.vehicleExpirationTime = vehicleExpirationTime;
	}

	public LocalDate getAuditTime() {
		return auditTime;
	}

	public void setAuditTime(LocalDate auditTime) {
		this.auditTime = auditTime;
	}

	public Integer getVehicleNature() {
		return vehicleNature;
	}

	public void setVehicleNature(Integer vehicleNature) {
		this.vehicleNature = vehicleNature;
	}

	public Integer getVehicleLineStatus() {
		return vehicleLineStatus;
	}

	public void setVehicleLineStatus(Integer vehicleLineStatus) {
		this.vehicleLineStatus = vehicleLineStatus;
	}

	public Integer getVehicleBelong() {
		return vehicleBelong;
	}

	public void setVehicleBelong(Integer vehicleBelong) {
		this.vehicleBelong = vehicleBelong;
	}

	public Integer getVehicleStatus() {
		return vehicleStatus;
	}

	public void setVehicleStatus(Integer vehicleStatus) {
		this.vehicleStatus = vehicleStatus;
	}

    


}
