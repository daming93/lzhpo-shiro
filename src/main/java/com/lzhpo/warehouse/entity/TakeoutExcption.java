package com.lzhpo.warehouse.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 物料品牌表
 * </p>
 *
 * @author xdm
 * @since 2020-08-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_takeout_excption")
public class TakeoutExcption extends DataEntity<TakeoutExcption> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项名称(仓储费选项）
     */
    private String name;
    
    @TableField("takeout_id")
    private String takeoutId;
    
    
	public String getTakeoutId() {
		return takeoutId;
	}

	public void setTakeoutId(String takeoutId) {
		this.takeoutId = takeoutId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



}
