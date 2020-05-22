package com.lzhpo.stock.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.TakeoutOperations;
/**
 * <p>
 * 入库操作表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-18
 */
public interface ITakeoutOperationsService extends IService<TakeoutOperations> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTakeoutOperationsCount(String name);
	
	//保存实例 返回该实例
	TakeoutOperations saveTakeoutOperations(TakeoutOperations takeoutOperations);

	//根据实例Id获取实例
	TakeoutOperations getTakeoutOperationsById(String id);

	//更新单条记录
	void updateTakeoutOperations(TakeoutOperations takeoutOperations);
	
	//删除一条记录 通常为软删
	void deleteTakeoutOperations(TakeoutOperations takeoutOperations);

	//选取所有记录
	List<TakeoutOperations> selectAll();

	//分页查询数据在父类

	//查询历史操作记录
	List<TakeoutOperations> selectByTakeoutId(String id);
}
