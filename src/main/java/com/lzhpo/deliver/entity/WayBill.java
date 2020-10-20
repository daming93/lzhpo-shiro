package com.lzhpo.deliver.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
import com.lzhpo.finance.entity.UserTable;
/**
 * <p>
 * 录单(和计划表基本一样，是计划表得主表，和统计内容)
 * </p>
 *
 * @author xdm
 * @since 2020-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_way_bill")
public class WayBill extends DataEntity<WayBill> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配送单系统编号
     */
    private String code;

    /**
     * 司机id
     */
    @TableField("driver_id")
    private String driverId;

    /**
     * 车辆id
     */
    @TableField("vehicle_num")
    private Integer vehicleNum;

    /**
     * 车次
     */
    @TableField("train_number")
    private Integer trainNumber;

    /**
     * 打印次数
     */
    @TableField("trip_number")
    private Integer tripNumber;

    /**
     * 体积
     */
    @TableField("dispatch_volume")
    private BigDecimal dispatchVolume;

    /**
     * 重量
     */
    @TableField("dispatch_weight")
    private BigDecimal dispatchWeight;

    /**
     * 数量 零
     */
    @TableField("dispatch_scattered")
    private Integer dispatchScattered;

    /**
     * 数量 整
     */
    @TableField("dispatch_whole")
    private Integer dispatchWhole;

    /**
     * 装载率
     */
    @TableField("loda_rate")
    private BigDecimal lodaRate;

    /**
     * 配送点数
     */
    @TableField("dispatch_piont")
    private Integer dispatchPiont;

    /**
     * 配送区域名称
     */
    @TableField("dispatch_area_id")
    private String dispatchAreaId;

    /**
     * 状态(0:排单中 ; 1:待确认; 2:拣货中; 3:配送中; 4:配送完成)
     */
    @TableField("dispatch_status")
    private Integer dispatchStatus;

    /**
     * 配送时间
     */
    @TableField("distribution_time")
    private LocalDate distributionTime;

    /**
     * 取货时间
     */
    @TableField("pickup_time")
    private LocalDateTime pickupTime;

    /**
     * 完成配送的人员
     */
    @TableField("delivery_complete_people")
    private String deliveryCompletePeople;

    /**
     * 配送完成时间
     */
    @TableField("delivery_complete_time")
    private LocalDateTime deliveryCompleteTime;

    /**
     * 是否 支付 0未支付  1已支付
     */
    @TableField("is_pay")
    private Integer isPay;

    /**
     * 是否计费
     */
    private Integer charging;

    /**
     * tms订单状态(1:非外埠;2:外埠，tms订单默认是外埠)
     */
    @TableField("type_cities_status")
    private Integer typeCitiesStatus;


    /**
     * 承重率
     */
    @TableField("weight_rate")
    private BigDecimal weightRate;

    /**
     * 修改状态（1待确认2可撤销3锁定）
     */
    private Integer status;

    @TableField(exist=false)
    private UserTable userTable;
    
    
	public UserTable getUserTable() {
		return userTable;
	}

	public void setUserTable(UserTable userTable) {
		this.userTable = userTable;
	}
	
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDriverId() {
		return driverId;
	}

	public void setDriverId(String driverId) {
		this.driverId = driverId;
	}

	

	public Integer getVehicleNum() {
		return vehicleNum;
	}

	public void setVehicleNum(Integer vehicleNum) {
		this.vehicleNum = vehicleNum;
	}

	public Integer getTrainNumber() {
		return trainNumber;
	}

	public void setTrainNumber(Integer trainNumber) {
		this.trainNumber = trainNumber;
	}

	public Integer getTripNumber() {
		return tripNumber;
	}

	public void setTripNumber(Integer tripNumber) {
		this.tripNumber = tripNumber;
	}

	public BigDecimal getDispatchVolume() {
		return dispatchVolume;
	}

	public void setDispatchVolume(BigDecimal dispatchVolume) {
		this.dispatchVolume = dispatchVolume;
	}

	public BigDecimal getDispatchWeight() {
		return dispatchWeight;
	}

	public void setDispatchWeight(BigDecimal dispatchWeight) {
		this.dispatchWeight = dispatchWeight;
	}

	public Integer getDispatchScattered() {
		return dispatchScattered;
	}

	public void setDispatchScattered(Integer dispatchScattered) {
		this.dispatchScattered = dispatchScattered;
	}

	public Integer getDispatchWhole() {
		return dispatchWhole;
	}

	public void setDispatchWhole(Integer dispatchWhole) {
		this.dispatchWhole = dispatchWhole;
	}

	public BigDecimal getLodaRate() {
		return lodaRate;
	}

	public void setLodaRate(BigDecimal lodaRate) {
		this.lodaRate = lodaRate;
	}

	public Integer getDispatchPiont() {
		return dispatchPiont;
	}

	public void setDispatchPiont(Integer dispatchPiont) {
		this.dispatchPiont = dispatchPiont;
	}

	public String getDispatchAreaId() {
		return dispatchAreaId;
	}

	public void setDispatchAreaId(String dispatchAreaId) {
		this.dispatchAreaId = dispatchAreaId;
	}

	public Integer getDispatchStatus() {
		return dispatchStatus;
	}

	public void setDispatchStatus(Integer dispatchStatus) {
		this.dispatchStatus = dispatchStatus;
	}

	public LocalDate getDistributionTime() {
		return distributionTime;
	}

	public void setDistributionTime(LocalDate distributionTime) {
		this.distributionTime = distributionTime;
	}

	public LocalDateTime getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(LocalDateTime pickupTime) {
		this.pickupTime = pickupTime;
	}

	public String getDeliveryCompletePeople() {
		return deliveryCompletePeople;
	}

	public void setDeliveryCompletePeople(String deliveryCompletePeople) {
		this.deliveryCompletePeople = deliveryCompletePeople;
	}

	public LocalDateTime getDeliveryCompleteTime() {
		return deliveryCompleteTime;
	}

	public void setDeliveryCompleteTime(LocalDateTime deliveryCompleteTime) {
		this.deliveryCompleteTime = deliveryCompleteTime;
	}

	public Integer getIsPay() {
		return isPay;
	}

	public void setIsPay(Integer isPay) {
		this.isPay = isPay;
	}

	public Integer getCharging() {
		return charging;
	}

	public void setCharging(Integer charging) {
		this.charging = charging;
	}

	public Integer getTypeCitiesStatus() {
		return typeCitiesStatus;
	}

	public void setTypeCitiesStatus(Integer typeCitiesStatus) {
		this.typeCitiesStatus = typeCitiesStatus;
	}

	public BigDecimal getWeightRate() {
		return weightRate;
	}

	public void setWeightRate(BigDecimal weightRate) {
		this.weightRate = weightRate;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}


    
}