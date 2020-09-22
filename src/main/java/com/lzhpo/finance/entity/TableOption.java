package com.lzhpo.finance.entity;

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
 * 自定义收入表选项
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("finance_table_option")
public class TableOption extends DataEntity<TableOption> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 选项名称(仓储费选项）
     */
    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
