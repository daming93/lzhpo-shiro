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
 * 储位表
 * </p>
 *
 * @author xdm
 * @since 2020-04-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("warehouse_tray")
public class Tray extends DataEntity<Tray> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 拼起来的code
     */
    private String code;

    /**
     * 分段A
     */
    @TableField("subsection_a")
    private String subsectionA;

    /**
     * 分段B
     */
    @TableField("subsection_b")
    private String subsectionB;

    /**
     * 分段C
     */
    @TableField("subsection_c")
    private String subsectionC;

    /**
     * 分段D
     */
    @TableField("subsection_d")
    private String subsectionD;

    /**
     * 分段E
     */
    @TableField("subsection_e")
    private String subsectionE;

    /**
     * 分段F
     */
    @TableField("subsection_f")
    private String subsectionF;

    /**
     * 分段F
     */
    @TableField("subsection_g")
    private String subsectionG;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getSubsectionA() {
		return subsectionA;
	}

	public void setSubsectionA(String subsectionA) {
		this.subsectionA = subsectionA;
	}

	public String getSubsectionB() {
		return subsectionB;
	}

	public void setSubsectionB(String subsectionB) {
		this.subsectionB = subsectionB;
	}

	public String getSubsectionC() {
		return subsectionC;
	}

	public void setSubsectionC(String subsectionC) {
		this.subsectionC = subsectionC;
	}

	public String getSubsectionD() {
		return subsectionD;
	}

	public void setSubsectionD(String subsectionD) {
		this.subsectionD = subsectionD;
	}

	public String getSubsectionE() {
		return subsectionE;
	}

	public void setSubsectionE(String subsectionE) {
		this.subsectionE = subsectionE;
	}

	public String getSubsectionF() {
		return subsectionF;
	}

	public void setSubsectionF(String subsectionF) {
		this.subsectionF = subsectionF;
	}

	public String getSubsectionG() {
		return subsectionG;
	}

	public void setSubsectionG(String subsectionG) {
		this.subsectionG = subsectionG;
	}

   


}
