package com.lzhpo.stock.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 出库表
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_takeout")
public class Takeout extends DataEntity<Takeout> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户Id
     */
    @TableField("client_id")
    private String clientId;
    

    /**
     * 客户Id
     */
    @TableField("excption_id")
    private String excptionId;


    /**
     * 送达方ID
     */
    @TableField("address_id")
    private String addressId;

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
     * 出库日期
     */
    @TableField("takeout_time")
    private LocalDate takeoutTime;

    /**
     * 配送日期
     */
    @TableField("delivery_time")
    private LocalDate deliveryTime;

    /**
     * 发货总数(整)
     */
    private Integer total;
    /**
     * 散数量
     */
    @TableField("scattered_num")
    private Integer scatteredNum;

    /**
     * 发货总数(零)
     */
    private Integer number;

    /**
     * 配送类型 //0市内 1 外阜 2自提
     */
    @TableField("deliver_type")
    private Integer deliverType;

    
    /**
     * 实际数量
     */
    @TableField("actual_amount")
    private String actualAmount;

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
    @TableField("tray_number")
    private Integer trayNumber;

    /**
     * 货值
     */
    private BigDecimal money;

    /**
     * 二次排车状态(0:否; 1:是)
     */
    @TableField("tow_status")
    private Integer towStatus;

    /**
     * 运输方式（1正常2加急3节假日）
     */
    @TableField("transportation_type")
    private Integer transportationType;

    /**
     * 修改状态（1待确认; 2待出库; 3锁定）
     */
    private Integer status;

    /**
     * 排单状态（0:未排单; 1:已排单; 2:待确认; 3:配送中; 4:配送完成; 5:配送异常; 6:二次配送 ）
     */
    @TableField("scheduling_status")
    private Integer schedulingStatus;

    /**
     * 打印次数
     */
    @TableField("print_num")
    private Integer printNum;

    /**
     * 备用
     */
    private String spare;

    /**
     * 拣货单号
     */
    @TableField("picking_code")
    private String pickingCode;

    /**
     * 拣货状态
     */
    @TableField("picking_status")
    private Integer pickingStatus;

    /**
     * 拣货单打印次数
     */
    @TableField("picking_print_number")
    private Integer pickingPrintNumber;

    @TableField("picking_people")
    private Long pickingPeople;

    /**
     * 存放地点(不使用id)
     */
    @TableField("picking_place")
    private String pickingPlace;

    /**
     * 拣货完成时间
     */
    @TableField("picking_time")
    private LocalDateTime pickingTime;
    
    @TableField("income_id")
    private String incomeId;
    
    /**
     * 拣货单打印次数
     */
    @TableField("adjustment")
    private Integer adjustment;
    
    @TableField(exist = false)
    private String statusStr;

    @TableField(exist = false)
    private String pickStatusStr;
    
    @TableField(exist = false)
    private String addressName;
    
    @TableField(exist = false)
    private List<TakeoutDetail> detailSet;
    
    @TableField(exist = false)
    private String transportationTypeStr;
    /**
     * 1 是拆分 null是不拆分 得单子 用于调度拆单使用
     */
    private Integer split;
    
	public String getIncomeId() {
		return incomeId;
	}

	public void setIncomeId(String incomeId) {
		this.incomeId = incomeId;
	}

	public Integer getDeliverType() {
		return deliverType;
	}

	public void setDeliverType(Integer deliverType) {
		this.deliverType = deliverType;
	}

	public String getTransportationTypeStr() {
		return transportationTypeStr;
	}

	public void setTransportationTypeStr(String transportationTypeStr) {
		this.transportationTypeStr = transportationTypeStr;
	}

	public String getAddressName() {
		return addressName;
	}

	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}

	public Integer getSplit() {
		return split;
	}

	public void setSplit(Integer split) {
		this.split = split;
	}

	public String getExcptionId() {
		return excptionId;
	}

	public void setExcptionId(String excptionId) {
		this.excptionId = excptionId;
	}

	public Integer getScatteredNum() {
		return scatteredNum;
	}

	public void setScatteredNum(Integer scatteredNum) {
		this.scatteredNum = scatteredNum;
	}

	public String getPickStatusStr() {
		return pickStatusStr;
	}

	public void setPickStatusStr(String pickStatusStr) {
		this.pickStatusStr = pickStatusStr;
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

	public List<TakeoutDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(List<TakeoutDetail> detailSet) {
		this.detailSet = detailSet;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
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

	public LocalDate getTakeoutTime() {
		return takeoutTime;
	}

	public void setTakeoutTime(LocalDate takeoutTime) {
		this.takeoutTime = takeoutTime;
	}

	public LocalDate getDeliveryTime() {
		return deliveryTime;
	}

	public void setDeliveryTime(LocalDate deliveryTime) {
		this.deliveryTime = deliveryTime;
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

	public Integer getTrayNumber() {
		return trayNumber;
	}

	public void setTrayNumber(Integer trayNumber) {
		this.trayNumber = trayNumber;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public Integer getTowStatus() {
		return towStatus;
	}

	public void setTowStatus(Integer towStatus) {
		this.towStatus = towStatus;
	}

	public Integer getTransportationType() {
		return transportationType;
	}

	public void setTransportationType(Integer transportationType) {
		this.transportationType = transportationType;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getSchedulingStatus() {
		return schedulingStatus;
	}

	public void setSchedulingStatus(Integer schedulingStatus) {
		this.schedulingStatus = schedulingStatus;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public String getSpare() {
		return spare;
	}

	public void setSpare(String spare) {
		this.spare = spare;
	}

	public String getPickingCode() {
		return pickingCode;
	}

	public void setPickingCode(String pickingCode) {
		this.pickingCode = pickingCode;
	}

	public Integer getPickingStatus() {
		return pickingStatus;
	}

	public void setPickingStatus(Integer pickingStatus) {
		this.pickingStatus = pickingStatus;
	}

	public Integer getPickingPrintNumber() {
		return pickingPrintNumber;
	}

	public void setPickingPrintNumber(Integer pickingPrintNumber) {
		this.pickingPrintNumber = pickingPrintNumber;
	}

	public Long getPickingPeople() {
		return pickingPeople;
	}

	public void setPickingPeople(Long pickingPeople) {
		this.pickingPeople = pickingPeople;
	}

	public String getPickingPlace() {
		return pickingPlace;
	}

	public void setPickingPlace(String pickingPlace) {
		this.pickingPlace = pickingPlace;
	}

	public LocalDateTime getPickingTime() {
		return pickingTime;
	}

	public void setPickingTime(LocalDateTime pickingTime) {
		this.pickingTime = pickingTime;
	}




}
