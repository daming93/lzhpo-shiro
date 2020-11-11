package com.lzhpo.client.mapper;

import com.lzhpo.client.entity.DeliverContractMain;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
public interface DeliverContractMainMapper extends BaseMapper<DeliverContractMain> {

	/**
	 * 获取客户正在使用得配送合同id
	 * @param clientId
	 * @return
	 */
	String getUsingContractId(String clientId);
}
