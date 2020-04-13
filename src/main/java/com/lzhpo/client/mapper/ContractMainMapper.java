package com.lzhpo.client.mapper;

import com.lzhpo.client.entity.ContractMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
public interface ContractMainMapper extends BaseMapper<ContractMain> {

	/**
	 * 获取客户正在使用得合同id
	 * @param clientId
	 * @return
	 */
	String getUsingContractId(String clientId);
}
