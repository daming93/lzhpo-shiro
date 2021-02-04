package com.lzhpo.deliver.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.deliver.entity.Dispatch;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
public interface IDispatchService extends IService<Dispatch> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDispatchCount(String name);
	
	//保存实例 返回该实例
	Dispatch saveDispatch(Dispatch dispatch);

	//根据实例Id获取实例
	Dispatch getDispatchById(String id);

	//更新单条记录
	void updateDispatch(Dispatch dispatch);
	
	//删除一条记录 通常为软删
	void deleteDispatch(Dispatch dispatch);

	//选取所有记录
	List<Dispatch> selectAll();

	//分页查询数据在父类
	//撤回
	public Dispatch backDispatch(String id);

	//清除主表关系  撤销路单
	int backDispatchByWaybillId(String wayBillId);
	
	//选取所有记录
	List<Dispatch> selectByWayBillId(String wayBillId);
	
	List<Dispatch> selectByCode(String code);
}
