package com.lzhpo.material.item.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 城坤品项表
 * </p>
 *
 * @author xdm
 * @since 2020-04-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("material_item_clientitem")
public class Clientitem extends DataEntity<Clientitem> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客户
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 编号
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 分类
     */
    private String category;

    /**
     * 品牌
     */
    private String brand;

    /**
     * 品名规格
     */
    private String sku;

    /**
     * 长度
     */
    @TableField("item_length")
    private BigDecimal itemLength;

    /**
     * 宽度
     */
    @TableField("item_width")
    private BigDecimal itemWidth;

    /**
     * 高度
     */
    @TableField("item_height")
    private BigDecimal itemHeight;

    /**
     * 重量
     */
    @TableField("item_weight")
    private BigDecimal itemWeight;

    /**
     * 材积
     */
    @TableField("item_volume")
    private BigDecimal itemVolume;

    /**
     * 单位（整）
     */
    @TableField("unit_whole")
    private String unitWhole;

    /**
     * 单位（零）
     */
    @TableField("unit_scattered")
    private String unitScattered;

    /**
     * 件/托
     */
    private Integer tray;

    /**
     * 换算率
     */
    @TableField("unit_rate")
    private Integer unitRate;

    /**
     * 保质天数
     */
    private Integer day;

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public BigDecimal getItemLength() {
		return itemLength;
	}

	public void setItemLength(BigDecimal itemLength) {
		this.itemLength = itemLength;
	}

	public BigDecimal getItemWidth() {
		return itemWidth;
	}

	public void setItemWidth(BigDecimal itemWidth) {
		this.itemWidth = itemWidth;
	}

	public BigDecimal getItemHeight() {
		return itemHeight;
	}

	public void setItemHeight(BigDecimal itemHeight) {
		this.itemHeight = itemHeight;
	}

	public BigDecimal getItemWeight() {
		return itemWeight;
	}

	public void setItemWeight(BigDecimal itemWeight) {
		this.itemWeight = itemWeight;
	}

	public BigDecimal getItemVolume() {
		return itemVolume;
	}

	public void setItemVolume(BigDecimal itemVolume) {
		this.itemVolume = itemVolume;
	}

	public String getUnitWhole() {
		return unitWhole;
	}

	public void setUnitWhole(String unitWhole) {
		this.unitWhole = unitWhole;
	}

	public String getUnitScattered() {
		return unitScattered;
	}

	public void setUnitScattered(String unitScattered) {
		this.unitScattered = unitScattered;
	}

	public Integer getTray() {
		return tray;
	}

	public void setTray(Integer tray) {
		this.tray = tray;
	}

	public Integer getUnitRate() {
		return unitRate;
	}

	public void setUnitRate(Integer unitRate) {
		this.unitRate = unitRate;
	}

	public Integer getDay() {
		return day;
	}

	public void setDay(Integer day) {
		this.day = day;
	}

    

}
