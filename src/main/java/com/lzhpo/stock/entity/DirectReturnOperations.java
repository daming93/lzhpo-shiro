package com.lzhpo.stock.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.lzhpo.common.base.DataEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
/**
 * <p>
 * 直退操作表
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("stock_direct_return_operations")
public class DirectReturnOperations extends DataEntity<DirectReturnOperations> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 退货单id
     */
    @TableField("return_id")
    private String returnId;

    /**
     * 操作类型(1新建2确定3撤销4增加费用5修改费用6删除费用)
     */
    private Integer type;

    /**
     * 操作对应的id
     */
    @TableField("operation_id")
    private String operationId;

    @TableField(exist = false)
    private String typeStr;
    
	public String getTypeStr() {
		return typeStr;
	}

	public void setTypeStr(String typeStr) {
		this.typeStr = typeStr;
	}

	public String getReturnId() {
		return returnId;
	}

	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOperationId() {
		return operationId;
	}

	public void setOperationId(String operationId) {
		this.operationId = operationId;
	}

}
