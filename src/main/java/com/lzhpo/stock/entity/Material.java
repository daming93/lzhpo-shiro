package com.lzhpo.stock.entity;

import java.io.Serializable;
import java.time.LocalDate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 仓储明细表
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_material")
public class Material extends DataEntity<Material> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 储位id
     */
    private String depot;

    /**
     * 托盘id
     */
    private String tray;

    /**
     * 系统品相id
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 批次号
     */
    @TableField("batch_number")
    private LocalDate batchNumber;

    /**
     * 库存数量
     */
    @TableField("depot_code")
    private Integer depotCode;

    /**
     * 锁定库存
     */
    @TableField("lock_code")
    private Integer lockCode;

    /**
     * 可使用库存
     */
    @TableField("available_num")
    private Integer availableNum;

    /**
     * 物料状态(1良品2不良品）
     */
    private Integer type;

    /**
     * 物料冻结状态
     */
    @TableField("material_status")
    private String materialStatus;

    @TableField(exist=false)
    private String numZ;
    
    @TableField(exist=false)
    private String rate;
    
    @TableField(exist=false)
    private String systemCode;
    
    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private String clientName;
    
    @TableField(exist=false)
    private String materialId;
    
    @TableField(exist=false)
    private String typeStr;
    
	
    /**
     * 整数量
     */
    @TableField("whole_num")
    private Integer wholeNum;
    /**
     * 散数量
     */
    @TableField("scattered_num")
    private Integer scatteredNum;
    
    
	public Integer getWholeNum() {
		return wholeNum;
	}

	public void setWholeNum(Integer wholeNum) {
		this.wholeNum = wholeNum;
	}

	public Integer getScatteredNum() {
		return scatteredNum;
	}

	public void setScatteredNum(Integer scatteredNum) {
		this.scatteredNum = scatteredNum;
	}
    
	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getNumZ() {
		return numZ;
	}

	public void setNumZ(String numZ) {
		this.numZ = numZ;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getSystemCode() {
		return systemCode;
	}

	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}

	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getDepot() {
		return depot;
	}

	public void setDepot(String depot) {
		this.depot = depot;
	}

	public String getTray() {
		return tray;
	}

	public void setTray(String tray) {
		this.tray = tray;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public LocalDate getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(LocalDate batchNumber) {
		this.batchNumber = batchNumber;
	}

	public Integer getDepotCode() {
		return depotCode;
	}

	public void setDepotCode(Integer depotCode) {
		this.depotCode = depotCode;
	}

	public Integer getLockCode() {
		return lockCode;
	}

	public void setLockCode(Integer lockCode) {
		this.lockCode = lockCode;
	}

	public Integer getAvailableNum() {
		return availableNum;
	}

	public void setAvailableNum(Integer availableNum) {
		this.availableNum = availableNum;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getMaterialStatus() {
		return materialStatus;
	}

	public void setMaterialStatus(String materialStatus) {
		this.materialStatus = materialStatus;
	}

   


}
