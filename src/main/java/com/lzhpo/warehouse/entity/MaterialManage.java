package com.lzhpo.warehouse.entity;

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
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_material_manage")
public class MaterialManage extends DataEntity<MaterialManage> implements Serializable {

    private static final long serialVersionUID = 1L;

    
    private LocalDate time;
    /**
     * 客户Id
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 客户通知单号
     */
    @TableField("client_code")
    private String clientCode;

    /**
     * 系统通知单号
     */
    @TableField("system_code")
    private String systemCode;

    /**
     * 状态（1转良，2转不良，3返厂，4报废)
     */
    private Integer status;

    /**
     * 修改状态(1待确认3锁定）
     */
    @TableField("modify_status")
    private Integer modifyStatus;
    
    @TableField(exist = false)
    private Set<MaterialManageDetail> detailSet;
    
    @TableField(exist = false)
    private String statusStr;
    
    @TableField(exist = false)
    private String modifyStatusStr;
    
    @TableField(exist = false)
    private String clientName;
    
    

	public String getStatusStr() {
		return statusStr;
	}

	public void setStatusStr(String statusStr) {
		this.statusStr = statusStr;
	}

	public String getModifyStatusStr() {
		return modifyStatusStr;
	}

	public void setModifyStatusStr(String modifyStatusStr) {
		this.modifyStatusStr = modifyStatusStr;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Set<MaterialManageDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<MaterialManageDetail> detailSet) {
		this.detailSet = detailSet;
	}


	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientCode() {
		return clientCode;
	}

	public void setClientCode(String clientCode) {
		this.clientCode = clientCode;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getModifyStatus() {
		return modifyStatus;
	}

	public void setModifyStatus(Integer modifyStatus) {
		this.modifyStatus = modifyStatus;
	}

	public LocalDate getTime() {
		return time;
	}

	public void setTime(LocalDate time) {
		this.time = time;
	}
	
}
