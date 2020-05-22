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
 * 物料和托盘对应表
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_material_tray")
public class MaterialTray extends DataEntity<MaterialTray> implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId("material_id")
    private String materialId;

    @TableField("tray_id")
    private String trayId;

    private Integer number;

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public String getTrayId() {
		return trayId;
	}

	public void setTrayId(String trayId) {
		this.trayId = trayId;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}


}
