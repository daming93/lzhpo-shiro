package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.StorageOperations;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 入库操作表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface IStorageOperationsService extends IService<StorageOperations> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getStorageOperationsCount(String name);
	
	//保存实例 返回该实例
	StorageOperations saveStorageOperations(StorageOperations storageOperations);

	//根据实例Id获取实例
	StorageOperations getStorageOperationsById(String id);

	//更新单条记录
	void updateStorageOperations(StorageOperations storageOperations);
	
	//删除一条记录 通常为软删
	void deleteStorageOperations(StorageOperations storageOperations);

	//选取所有记录
	List<StorageOperations> selectAll();

	//分页查询数据在父类

	//查询历史操作记录
	List<StorageOperations> selectByStorageId(String id);
}
