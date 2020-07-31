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
 * 仓储明细表-操作表可以相当于流水表
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_material_operations")
public class MaterialOperations extends DataEntity<MaterialOperations> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实时库存表id
     */
    @TableField("material_id")
    private String materialId;

    /**
     * 来源编号
     */
    @TableField("from_code")
    private String fromCode;

    /**
     * 锁定库存
     */
    @TableField("from_type")
    private Integer fromType;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 加减(1加2减）
     */
    private Integer type;
    
    @TableField(exist = false)
    private String typeStr;
    
    @TableField(exist=false)
    private String numZ;
    
    @TableField(exist=false)
    private String rate;
    
    @TableField(exist=false)
    private String systemCode;
    
    @TableField(exist=false)
    private String itemName;
    
    @TableField(exist=false)
    private LocalDate batch;

    @TableField(exist=false)
    private String fromTypeStr;
    
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
    
    @TableField(exist=false)
    private String clientName;
    
    
    
	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

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
	public String getFromTypeStr() {
		return fromTypeStr;
	}

	public void setFromTypeStr(String fromTypeStr) {
		this.fromTypeStr = fromTypeStr;
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

	public LocalDate getBatch() {
		return batch;
	}

	public void setBatch(LocalDate batch) {
		this.batch = batch;
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

	public String getFromCode() {
		return fromCode;
	}

	public void setFromCode(String fromCode) {
		this.fromCode = fromCode;
	}

	public Integer getFromType() {
		return fromType;
	}

	public void setFromType(Integer fromType) {
		this.fromType = fromType;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

    

}
