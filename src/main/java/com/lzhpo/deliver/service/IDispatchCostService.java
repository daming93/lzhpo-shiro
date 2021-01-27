package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.DispatchCost;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 配送计划支出表 司机运费 服务类
 * </p>
 *
 * @author xdm
 * @since 2021-01-22
 */
public interface IDispatchCostService extends IService<DispatchCost> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDispatchCostCount(String name);
	
	//保存实例 返回该实例
	DispatchCost saveDispatchCost(DispatchCost dispatchCost);

	//根据实例Id获取实例
	DispatchCost getDispatchCostById(String id);

	//更新单条记录
	void updateDispatchCost(DispatchCost dispatchCost);
	
	//删除一条记录 通常为软删
	void deleteDispatchCost(DispatchCost dispatchCost);

	//选取所有记录
	List<DispatchCost> selectAll();

	//分页查询数据在父类

	void deleteByDispatchId(String dispatchId);
}
