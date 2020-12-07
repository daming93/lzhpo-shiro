package com.lzhpo.stock.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 物料和储位对应表
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_material_depot")
public class MaterialDepot extends DataEntity<MaterialDepot> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableField("material_id")
    private String materialId;

    @TableField("depot_id")
    private String depotId;

    private Integer number;

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
    
    @TableField(exist=false)
    private Integer spilt ;//用于标记此单是否拆单
    
    
	public Integer getSpilt() {
		return spilt;
	}

	public void setSpilt(Integer spilt) {
		this.spilt = spilt;
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

	public String getDepotId() {
		return depotId;
	}

	public void setDepotId(String depotId) {
		this.depotId = depotId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}


}
