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
 * 唯一编号
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_generate_no")

public class GenerateNo  extends DataEntity<GenerateNo> implements Serializable {


    private static final long serialVersionUID = 1L;

    /**
     * 单号前缀
     */
    private String prefix;

    /**
     * 流水号
     */
    @TableField("serial_no")
    private Integer serialNo;

    /**
     * 创建日期
     */
    @TableField("date_no")
    private String dateNo;

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Integer getSerialNo() {
		return serialNo;
	}

	public void setSerialNo(Integer serialNo) {
		this.serialNo = serialNo;
	}

	public String getDateNo() {
		return dateNo;
	}

	public void setDateNo(String dateNo) {
		this.dateNo = dateNo;
	}

   







}
