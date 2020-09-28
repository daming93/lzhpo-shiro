package com.lzhpo.sys.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 用户的自定义设置(偏好设置表，目前用于偏好设计的财务附表)
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_setting")
public class UserSetting extends DataEntity<UserSetting> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;

    /**
     * 自定义设置id
     */
    @TableField("table_id")
    private String tableId;

    /**
     * 类型(1,自定义附表)
     */
    private Integer type;

    /**
     * 模块名称
     */
    @TableField("modular_name")
    private String modularName;

    /**
     * 模块
     */
    private Integer modular;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getModularName() {
		return modularName;
	}

	public void setModularName(String modularName) {
		this.modularName = modularName;
	}

	public Integer getModular() {
		return modular;
	}

	public void setModular(Integer modular) {
		this.modular = modular;
	}

   

}
