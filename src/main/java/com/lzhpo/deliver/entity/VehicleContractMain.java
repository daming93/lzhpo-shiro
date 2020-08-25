package com.lzhpo.deliver.entity;

import java.io.Serializable;
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
 * @since 2020-08-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("deliver_vehicle_contract_main")
public class VehicleContractMain extends DataEntity<VehicleContractMain> implements Serializable {

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
     * 保存文件名
     */
    @TableField("file_name")
    private String fileName;
    

	@TableField(exist = false)
    private Set<VehicleContractMainDetail> detailSet;
	

	public Set<VehicleContractMainDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<VehicleContractMainDetail> detailSet) {
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

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

    
}
