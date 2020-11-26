package com.lzhpo.customer.entity;

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
 * 客服责任部门名
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("customer_service_responsible_department")
public class CustomerServiceResponsibleDepartment extends DataEntity<CustomerServiceResponsibleDepartment> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 客服责任部门
     */
    private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
