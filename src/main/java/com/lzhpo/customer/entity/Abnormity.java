package com.lzhpo.customer.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 异常表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_abnormity")
public class Abnormity extends DataEntity<Abnormity> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 异常类型
     */
    @TableField("abnormity_type")
    private Integer abnormityType;

    /**
     * 异常原因
     */
    private String cause;

    @TableField(exist=false)
    private String abnormityTypeStr;
    
    /**
     * 异常类型
     */
    @TableField("abnormity_type_id")
    private String abnormityTypeId;
    
	public String getAbnormityTypeId() {
		return abnormityTypeId;
	}

	public void setAbnormityTypeId(String abnormityTypeId) {
		this.abnormityTypeId = abnormityTypeId;
	}

	public String getAbnormityTypeStr() {
		return abnormityTypeStr;
	}

	public void setAbnormityTypeStr(String abnormityTypeStr) {
		this.abnormityTypeStr = abnormityTypeStr;
	}

	public Integer getAbnormityType() {
		return abnormityType;
	}

	public void setAbnormityType(Integer abnormityType) {
		this.abnormityType = abnormityType;
	}

	public String getCause() {
		return cause;
	}

	public void setCause(String cause) {
		this.cause = cause;
	}

   

}
