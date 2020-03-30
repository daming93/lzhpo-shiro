package com.lzhpo.sys.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 字典表
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_dictionary")

public class Dictionary  extends DataEntity<Dictionary> implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 分类编码
     */
    @TableField("category_code")
    private String categoryCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 键
     */
    private String dkey;

    /**
     * 值
     */
    private Integer value;

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDkey() {
		return dkey;
	}

	public void setDkey(String dkey) {
		this.dkey = dkey;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

    







}
