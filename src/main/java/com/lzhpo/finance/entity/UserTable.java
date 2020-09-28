package com.lzhpo.finance.entity;

import java.io.Serializable;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 自定义收入表--用户使用（在不同表格中不同体现)
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_user_table")
public class UserTable extends DataEntity<UserTable> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项名目
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 模块名称
     */
    @TableField("modular_name")
    private String modularName;

    /**
     * 模块
     */
    private Integer modular;

    @TableField("table_id")
    private String tableId;

    /**
     * 是否审核
     */
    @TableField("is_audit")
    private Integer isAudit;

    @TableField(exist=false)
    private Set<UserTableDetail> detailSet;

	public Set<UserTableDetail> getDetailSet() {
		return detailSet;
	}

	public void setDetailSet(Set<UserTableDetail> detailSet) {
		this.detailSet = detailSet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public Integer getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}
}
