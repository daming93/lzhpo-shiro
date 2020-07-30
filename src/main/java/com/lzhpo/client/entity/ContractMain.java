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
 * @since 2020-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_contract_main")

public class ContractMain  extends DataEntity<ContractMain> implements Serializable {


    private static final long serialVersionUID = 1L;

    private String name;
    /**
     * 入库装卸费用
     */
    @TableField("handing_storage_money")
    private BigDecimal handingStorageMoney;

    /**
     * 出库装卸费用
     */
    @TableField("handing_takeout_money")
    private BigDecimal handingTakeoutMoney;

    /**
     * 终端装卸费
     */
    @TableField("handing_terminal_money")
    private BigDecimal handingTerminalMoney;

    /**
     * 装卸类型
     */
    @TableField("handing_type")
    private Integer handingType;

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
     * 是否整进整出客户 0 是 1不是
     */
    @TableField("is_whole")
    private Integer isWhole;

    
    /**
     * 合同开始日期
     */
    @TableField("start_time")
    private LocalDate startTime;

    /**
     * 合同截止日期
     */
    @TableField("over_time")
    private LocalDate overTime;

    /**
     * 客户id
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 合同编号
     */
    @TableField("contract_code")
    private String contractCode;


    /**
     * 保存文件名
     */
    @TableField("file_name")    
    private String fileName;
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

	

	public BigDecimal getHandingStorageMoney() {
		return handingStorageMoney;
	}

	public void setHandingStorageMoney(BigDecimal handingStorageMoney) {
		this.handingStorageMoney = handingStorageMoney;
	}

	public BigDecimal getHandingTakeoutMoney() {
		return handingTakeoutMoney;
	}

	public void setHandingTakeoutMoney(BigDecimal handingTakeoutMoney) {
		this.handingTakeoutMoney = handingTakeoutMoney;
	}

	public BigDecimal getHandingTerminalMoney() {
		return handingTerminalMoney;
	}

	public void setHandingTerminalMoney(BigDecimal handingTerminalMoney) {
		this.handingTerminalMoney = handingTerminalMoney;
	}

	public Integer getHandingType() {
		return handingType;
	}

	public void setHandingType(Integer handingType) {
		this.handingType = handingType;
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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getContractCode() {
		return contractCode;
	}

	public void setContractCode(String contractCode) {
		this.contractCode = contractCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public Integer getIsWhole() {
		return isWhole;
	}

	public void setIsWhole(Integer isWhole) {
		this.isWhole = isWhole;
	}


	@TableField(exist = false)
    private Set<ContractMainDetail> detailSet;

	public Set<ContractMainDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<ContractMainDetail> detailSet) {
		this.detailSet = detailSet;
	}
	
	
}
