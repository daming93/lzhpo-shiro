package com.lzhpo.finance.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 财务收入
 * </p>
 *
 * @author xdm
 * @since 2020-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_income")
public class Income extends DataEntity<Income> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编码
     */
    private String code;

    /**
     * 依据(一般为合同编号)
     */
    private String basis;

    /**
     * 费用产生人（一般是客户）
     */
    @TableField("client_id")
    private String clientId;

    /**
     * 费用项目
     */
    @TableField("option_id")
    private String optionId;

    /**
     * 金额
     */
    private BigDecimal moeny;

    /**
     * 审计金额
     */
    @TableField("audit_moeny")
    private BigDecimal auditMoeny;

    /**
     * 类型(默认1为客户收入)
     */
    private Integer type;

    /**
     * 审计人
     */
    @TableField("audit_man")
    private String auditMan;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBasis() {
		return basis;
	}

	public void setBasis(String basis) {
		this.basis = basis;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public BigDecimal getMoeny() {
		return moeny;
	}

	public void setMoeny(BigDecimal moeny) {
		this.moeny = moeny;
	}

	public BigDecimal getAuditMoeny() {
		return auditMoeny;
	}

	public void setAuditMoeny(BigDecimal auditMoeny) {
		this.auditMoeny = auditMoeny;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getAuditMan() {
		return auditMan;
	}

	public void setAuditMan(String auditMan) {
		this.auditMan = auditMan;
	}
}
