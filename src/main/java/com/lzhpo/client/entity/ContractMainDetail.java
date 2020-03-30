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
 * 合同收费项
 * </p>
 *
 * @author xdm
 * @since 2020-03-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("client_contract_main_detail")

public class ContractMainDetail  extends DataEntity<ContractMainDetail> implements Serializable {


    private static final long serialVersionUID = 1L;

    private Long money;

    private Integer type;

    /**
     * 角色名称
     */
    @TableField("option_id")
    private String optionId;

    /**
     * 合同Id(主表id)
     */
    @TableField("contract_id")
    private String contractId;
    
	public Long getMoney() {
		return money;
	}

	public void setMoney(Long money) {
		this.money = money;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getOptionId() {
		return optionId;
	}

	public void setOptionId(String optionId) {
		this.optionId = optionId;
	}

	public String getContractId() {
		return contractId;
	}

	public void setContractId(String contractId) {
		this.contractId = contractId;
	}

}
