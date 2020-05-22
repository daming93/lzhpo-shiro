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
 * 入库明细表
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_storage_detail")
public class StorageDetail extends DataEntity<StorageDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 入库表Id
     */
    @TableField("storage_id")
    private String storageId;
    /**
     * 物料
     */
    @TableField("material_id")
    private String materialId;

    /**
     * 物料品相id
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 品牌系列
     */
    private String brand;

    /**
     * 入库数量
     */
    private Integer number;

    /**
     * 储位
     */
    private String depot;

    /**
     * 托盘号
     */
    private String tray;

    /**
     * 批次号
     */
    private LocalDate batch;
    
    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private String rate;
    
    @TableField(exist=false)
    private String numZ;
    
    
    
	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

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

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getStorageId() {
		return storageId;
	}

	public void setStorageId(String storageId) {
		this.storageId = storageId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
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

    


}
