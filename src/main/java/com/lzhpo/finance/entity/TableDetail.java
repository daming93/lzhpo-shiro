package com.lzhpo.finance.entity;

import java.math.BigDecimal;
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
 * 自定义收入表明细
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_table_detail")
public class TableDetail extends DataEntity<TableDetail> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 自定义表id
     */
    @TableField("table_id")
    private String tableId;

    /**
     * 名目
     */
    @TableField("option_name")
    private String optionName;

    /**
     * 计算方式(0 增 1 减)
     */
    private Integer math;

    /**
     * 默认数值
     */
    @TableField("default_money")
    private BigDecimal defaultMoney;

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tableId) {
		this.tableId = tableId;
	}

	public String getOptionName() {
		return optionName;
	}

	public void setOptionName(String optionName) {
		this.optionName = optionName;
	}

	public Integer getMath() {
		return math;
	}

	public void setMath(Integer math) {
		this.math = math;
	}

	public BigDecimal getDefaultMoney() {
		return defaultMoney;
	}

	public void setDefaultMoney(BigDecimal defaultMoney) {
		this.defaultMoney = defaultMoney;
	}

  


}
