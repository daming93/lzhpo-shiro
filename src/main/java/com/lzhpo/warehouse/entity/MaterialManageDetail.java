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
 * 
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_material_manage_detail")
public class MaterialManageDetail extends DataEntity<MaterialManageDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 物料管理id
     */
    @TableField("manage_id")
    private String manageId;

    /**
     * 物料Id
     */
    @TableField("material_id")
    private String material;

    /**
     * 系统品相Id
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 储位
     */
    private String depot;

    /**
     * 托盘
     */
    private String tray;

    /**
     * 批次
     */
    private LocalDate batch;

    /**
     * 数量
     */
    private Integer number;

    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private String rate;
    
    @TableField(exist=false)
    private String numZ;
    
    //现有最大库存
    @TableField(exist=false)
    private Integer maxNumber;
    
    @TableField(exist=false)
    private String itemCode;
    

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getRate() {
		return rate;
	}

	public void setRate(String rate) {
		this.rate = rate;
	}

	public String getNumZ() {
		return numZ;
	}

	public void setNumZ(String numZ) {
		this.numZ = numZ;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getManageId() {
		return manageId;
	}

	public void setManageId(String manageId) {
		this.manageId = manageId;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String materialId) {
		this.material = materialId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
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

	public LocalDate getBatch() {
		return batch;
	}

	public void setBatch(LocalDate batch) {
		this.batch = batch;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}
}
