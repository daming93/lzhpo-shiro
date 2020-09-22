package com.lzhpo.finance.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.util.Set;

import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.lzhpo.common.base.DataEntity;
/**
 * <p>
 * 自定义收入表
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_table")
public class Table extends DataEntity<Table> implements Serializable {

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

    @TableField(exist=false)
    private Set<TableDetail> detailSets;
    
    /**
     * 是否审核
     */
    @TableField("is_audit")
    private Integer isAudit;

    
    
	public Integer getIsAudit() {
		return isAudit;
	}

	public void setIsAudit(Integer isAudit) {
		this.isAudit = isAudit;
	}

	public Set<TableDetail> getDetailSets() {
		return detailSets;
	}

	public void setDetailSets(Set<TableDetail> detailSets) {
		this.detailSets = detailSets;
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

    

}
