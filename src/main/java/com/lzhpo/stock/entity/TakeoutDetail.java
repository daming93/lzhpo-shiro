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
 * 
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_takeout_detail")
public class TakeoutDetail extends DataEntity<TakeoutDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户ID
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 出库表Id
     */
    @TableField("takeout_id")
    private String takeoutId;

    /**
     * 物料Id
     */
    private String material;

    /**
     * 物料系统代号
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 出库数量
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
    
    //现有最大库存
    @TableField(exist=false)
    private Integer maxNumber;
    
	public Integer getMaxNumber() {
		return maxNumber;
	}

	public void setMaxNumber(Integer maxNumber) {
		this.maxNumber = maxNumber;
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

	public String getTakeoutId() {
		return takeoutId;
	}

	public void setTakeoutId(String takeoutId) {
		this.takeoutId = takeoutId;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
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
