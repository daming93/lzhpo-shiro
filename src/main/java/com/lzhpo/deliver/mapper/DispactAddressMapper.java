package com.lzhpo.deliver.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzhpo.deliver.entity.DispactAddress;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
public interface DispactAddressMapper extends BaseMapper<DispactAddress> {

	public List<DispactAddress> getDispactWaitForDeliverBill(Map<String, Object> map);
	
	//库存单据得待排单据
	public List<DispactAddress> getDispactWaitForTakoutBill(Map<String, Object> map);
	
	public DispactAddress getDispactAddressByBillId(@Param("id") String id);
	
	public DispactAddress getDispactAddressByTakoutId(@Param("id") String id);
	
	public DispactAddress getDispactAddressByBillCode(@Param("code") String code);
}
