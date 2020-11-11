package com.lzhpo.client.entity;

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
 * 
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_deliver_contract_main")
public class DeliverContractMain extends DataEntity<DeliverContractMain> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 合同名称
     */
    private String name;

    /**
     * 计算类型
     */
    @TableField("math_type")
    private Integer mathType;

    /**
     * 是否整进整出客户 0 是 1不是
     */
    @TableField("is_whole")
    private Integer isWhole;

    /**
     * 文件id
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 是否审核
     */
    @TableField("is_audit")
    private Integer isAudit;

    /**
     * 合同开始日期(预留)
     */
    @TableField("start_time")
    private LocalDate startTime;

    /**
     * 合同截止日期(预留)
     */
    @TableField("over_time")
    private LocalDate overTime;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;

    /**
     * 加急运输方式的比例
     */
    @TableField("transportation_type_urgent")
    private BigDecimal transportationTypeUrgent;

    /**
     * 节日运输方式的比例
     */
    @TableField("transportation_type_holiday")
    private BigDecimal transportationTypeHoliday;

    /**
     * 托运运费比例
     */
    @TableField("transportation_type_consign")
    private BigDecimal transportationTypeConsign;

    /**
     * 合同名称
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 合同名称
     */
    @TableField("client_name")
    private String clientName;

    /**
     * 保存文件名
     */
    @TableField("file_name")
    private String fileName;


	@TableField(exist = false)
    private Set<DeliverContractMainDetail> detailSet;
	/**
	 * 是否在使用
	 */
	@TableField(exist = false)
    private boolean isUse;
	


	public boolean getIsUse() {
		return isUse;
	}

	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}

	
    
	public Set<DeliverContractMainDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<DeliverContractMainDetail> detailSet) {
		this.detailSet = detailSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getMathType() {
		return mathType;
	}

	public void setMathType(Integer mathType) {
		this.mathType = mathType;
	}

	public Integer getIsWhole() {
		return isWhole;
	}

	public void setIsWhole(Integer isWhole) {
		this.isWhole = isWhole;
	}

	public String getFileId() {
		return fileId;
	}

	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	public Integer getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}

	public LocalDate getStartTime() {
		return startTime;
	}

	public void setStartTime(LocalDate startTime) {
		this.startTime = startTime;
	}

	public LocalDate getOverTime() {
		return overTime;
	}

	public void setOverTime(LocalDate overTime) {
		this.overTime = overTime;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public BigDecimal getTransportationTypeUrgent() {
		return transportationTypeUrgent;
	}

	public void setTransportationTypeUrgent(BigDecimal transportationTypeUrgent) {
		this.transportationTypeUrgent = transportationTypeUrgent;
	}

	public BigDecimal getTransportationTypeHoliday() {
		return transportationTypeHoliday;
	}

	public void setTransportationTypeHoliday(BigDecimal transportationTypeHoliday) {
		this.transportationTypeHoliday = transportationTypeHoliday;
	}

	public BigDecimal getTransportationTypeConsign() {
		return transportationTypeConsign;
	}

	public void setTransportationTypeConsign(BigDecimal transportationTypeConsign) {
		this.transportationTypeConsign = transportationTypeConsign;
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

    

}
