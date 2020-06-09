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
 * 调仓物料表
 * </p>
 *
 * @author xdm
 * @since 2020-06-03
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_change_material")
public class ChangeMaterial extends DataEntity<ChangeMaterial> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 系统物料编号
     */
    @TableField("item_id")
    private String itemId;

    /**
     * 物料id
     */
    @TableField("material_id")
    private String materialId;

    /**
     * 批次
     */
    private LocalDate batch;

    /**
     * 原储位id
     */
    private String fdepot;

    /**
     * 原托盘id
     */
    private String ftray;

    /**
     * 现储位id
     */
    private String ndepot;

    /**
     * 现托盘id
     */
    private String ntray;

    /**
     * 原库存
     */
    private Integer oldnum;

    /**
     * 调整库存
     */
    private Integer nownum;

    @TableField(exist=false)
    private String itemCode;
    
    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private String clientName;
    
    
    
    
	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public LocalDate getBatch() {
		return batch;
	}

	public void setBatch(LocalDate batch) {
		this.batch = batch;
	}

	public String getFdepot() {
		return fdepot;
	}

	public void setFdepot(String fdepot) {
		this.fdepot = fdepot;
	}

	public String getFtray() {
		return ftray;
	}

	public void setFtray(String ftray) {
		this.ftray = ftray;
	}

	public String getNdepot() {
		return ndepot;
	}

	public void setNdepot(String ndepot) {
		this.ndepot = ndepot;
	}

	public String getNtray() {
		return ntray;
	}

	public void setNtray(String ntray) {
		this.ntray = ntray;
	}

	public Integer getOldnum() {
		return oldnum;
	}

	public void setOldnum(Integer oldnum) {
		this.oldnum = oldnum;
	}

	public Integer getNownum() {
		return nownum;
	}

	public void setNownum(Integer nownum) {
		this.nownum = nownum;
	}

    

}
