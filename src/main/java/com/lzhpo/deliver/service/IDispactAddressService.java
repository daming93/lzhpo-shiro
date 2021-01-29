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
	
	//分页查询数据在父类
	public List<DispactAddress> getDispactWaitForLineTakeoutBill(Map<String, Object> map);
	//分页查询数据在父类
	public List<DispactAddress> getDispactWaitForTakeoutBill(Map<String, Object> map);
	//根据实例Id获取实例 排单管理中得
	DispactAddress getDispactAddressByBillId(String id);
	//根据实例Id获取实例 排单管理中得库存出单
	DispactAddress getDispactAddressByTakoutId(String id);
	//根据实例Id获取实例 排单管理中得线路出单
	DispactAddress getDispactAddressByLineTakoutId(String id);
	//拆单
	public String splitBill(DispactAddress dispactAddress);
	
	public String splitCode(String clientCode,String code);
	
	public String changeBindingStatus(String disAddressId);
	
	//更新单条记录
	public String bindingStatus(DispactAddress dispactAddress);
	
	public long getCountByCodeAndIDispacthId(String code, String dispacthId);
	
	//分页查询数据在父类
	public List<DispactAddress> getListByDispatchId(String dispacthId);
	
	long countDetailByTableId(String tableId);
	
	//根据dispatchid去查询具体送往地址的分组
	long countNumDetailSendPlaceByDispatchId(String dispatchId);
	
	//根据dispatchid去查询具体送往区域的分组 
	List<DispactAddress> countNumDetailSendAreaByDispatchId(String dispatchId);
}
