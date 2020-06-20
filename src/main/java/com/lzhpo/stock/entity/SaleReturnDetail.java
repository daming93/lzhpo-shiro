package com.lzhpo.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 退货详情表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_sale_return_detail")
public class SaleReturnDetail extends DataEntity<SaleReturnDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退货id
     */
    @TableField("sales_return_id")
    private String salesReturnId;

    /**
     * 物料Id
     */
    @TableField("material_id")
    private String materialId;

    /**
     * 系统物料id
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

    /**
     * 出库数量
     */
    @TableField("takeout_num")
    private Integer takeoutNum;

    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private String rate;
    
    @TableField(exist=false)
    private String numZ;
    
    
    
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

	public String getSalesReturnId() {
		return salesReturnId;
	}

	public void setSalesReturnId(String salesReturnId) {
		this.salesReturnId = salesReturnId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
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

	public Integer getTakeoutNum() {
		return takeoutNum;
	}

	public void setTakeoutNum(Integer takeoutNum) {
		this.takeoutNum = takeoutNum;
	}

}
