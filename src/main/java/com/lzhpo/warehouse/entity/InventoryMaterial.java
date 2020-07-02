package com.lzhpo.warehouse.entity;

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
 * 盘点物料表
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_inventory_material")
public class InventoryMaterial extends DataEntity<InventoryMaterial> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 盘点id
     */
    @TableField("inventory_id")
    private String inventoryId;

    /**
     * 物料id
     */
    @TableField("material_id")
    private String materialId;

    /**
     * 储位
     */
    private String depot;

    /**
     * 托盘
     */
    private String tray;

    /**
     * 客户物料编号
     */
    private String code;

    /**
     * 物料名（名称+品名规格）
     */
    @TableField("supplies_sku")
    private String suppliesSku;

    /**
     * 批次号
     */
    @TableField("batch_number")
    private String batchNumber;

    /**
     * 保质期（1正常；2即将过期；3过期；）
     */
    @TableField("quality_status")
    private Integer qualityStatus;

    /**
     * 物料状态(1良品2不良品）
     */
    private Integer type;

    /**
     * 仓管盘点数量
     */
    @TableField("inventory_num")
    private Long inventoryNum;

    /**
     * 系统库存数量
     */
    @TableField("depot_num")
    private Long depotNum;

    /**
     * 差异数量
     */
    private Long difference;

    /**
     * 差异状态（1；待定  2；正常  3；差异）
     */
    @TableField("check_type")
    private Integer checkType;

    /**
     * 换算率
     */
    @TableField("unit_rate")
    private Integer unitRate;

    /**
     * 循环批次(1:第一天; 2:第二天; 3:第三天)
     */
    @TableField("cycle_batch")
    private Integer cycleBatch;
    
    @TableField(exist=false)
    private String checkTypeStr;

    
	public String getCheckTypeStr() {
		return checkTypeStr;
	}

	public void setCheckTypeStr(String checkTypeStr) {
		this.checkTypeStr = checkTypeStr;
	}

	public String getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(String inventoryId) {
		this.inventoryId = inventoryId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSuppliesSku() {
		return suppliesSku;
	}

	public void setSuppliesSku(String suppliesSku) {
		this.suppliesSku = suppliesSku;
	}

	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	public Integer getQualityStatus() {
		return qualityStatus;
	}

	public void setQualityStatus(Integer qualityStatus) {
		this.qualityStatus = qualityStatus;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Long getInventoryNum() {
		return inventoryNum;
	}

	public void setInventoryNum(Long inventoryNum) {
		this.inventoryNum = inventoryNum;
	}

	public Long getDepotNum() {
		return depotNum;
	}

	public void setDepotNum(Long depotNum) {
		this.depotNum = depotNum;
	}

	public Long getDifference() {
		return difference;
	}

	public void setDifference(Long difference) {
		this.difference = difference;
	}

	public Integer getCheckType() {
		return checkType;
	}

	public void setCheckType(Integer checkType) {
		this.checkType = checkType;
	}

	public Integer getUnitRate() {
		return unitRate;
	}

	public void setUnitRate(Integer unitRate) {
		this.unitRate = unitRate;
	}

	public Integer getCycleBatch() {
		return cycleBatch;
	}

	public void setCycleBatch(Integer cycleBatch) {
		this.cycleBatch = cycleBatch;
	}

 


}
