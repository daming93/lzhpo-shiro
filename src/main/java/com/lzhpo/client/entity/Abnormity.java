package com.lzhpo.client.entity;

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
 * 异常表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_abnormity")
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
