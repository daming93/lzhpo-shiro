package com.lzhpo.finance.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 财务收入
 * </p>
 *
 * @author xdm
 * @since 2020-04-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_income")
public class Income implements Serializable {

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

    @TableField("create_date")
    private LocalDateTime createDate;

    @TableField("create_by")
    private String createBy;

    @TableField("update_date")
    private LocalDateTime updateDate;

    @TableField("update_by")
    private String updateBy;

    private String remarks;

    @TableField("del_flag")
    private Integer delFlag;


}
