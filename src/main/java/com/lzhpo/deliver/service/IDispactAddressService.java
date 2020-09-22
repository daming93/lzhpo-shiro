package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.DispactAddress;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
import java.util.Map;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
public interface IDispactAddressService extends IService<DispactAddress> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDispactAddressCount(String name);
	
	//保存实例 返回该实例
	DispactAddress saveDispactAddress(DispactAddress dispactAddress);

	//根据实例Id获取实例
	DispactAddress getDispactAddressById(String id);

	//更新单条记录
	void updateDispactAddress(DispactAddress dispactAddress);
	
	//删除一条记录 通常为软删
	void deleteDispactAddress(DispactAddress dispactAddress);

	//选取所有记录
	List<DispactAddress> selectAll();

	//分页查询数据在父类
	public List<DispactAddress> getDispactWaitForDeliverBill(Map<String, Object> map);
	
	//根据实例Id获取实例 排单管理中得
	DispactAddress getDispactAddressByBillId(String id);
	
	//拆单
	public String splitBill(DispactAddress dispactAddress);
	
	public String splitCode(String code);
	
	public String changeBindingStatus(String disAddressId);
	
	//更新单条记录
	public String bindingStatus(DispactAddress dispactAddress);
	
	public long getCountByCodeAndIDispacthId(String code, String dispacthId);
}
