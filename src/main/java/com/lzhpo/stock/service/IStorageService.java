package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.Storage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 入库主表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface IStorageService extends IService<Storage> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getStorageCount(String name);
	
	//保存实例 返回该实例
	Storage saveStorage(Storage storage);

	//根据实例Id获取实例
	Storage getStorageById(String id);

	//更新单条记录
	void updateStorage(Storage storage);
	
	//删除一条记录 通常为软删
	void deleteStorage(Storage storage);

	//选取所有记录
	List<Storage> selectAll();

	//分页查询数据在父类

	MathStockNumber selectMathStorageNumberByStorageId(String storageId);
	
	
	//撤回入库单
	void backStorage(String storageId);
	
	//锁住单据 如果出库用了某单的物品
	
	void lockStorage(String materialId);
	
}
