package com.lzhpo.warehouse.service;

import com.lzhpo.warehouse.entity.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 盘点表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
public interface IInventoryService extends IService<Inventory> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getInventoryCount(String name);
	
	//保存实例 返回该实例
	Inventory saveInventory(Inventory inventory);

	//根据实例Id获取实例
	Inventory getInventoryById(String id);

	//更新单条记录
	void updateInventory(Inventory inventory);
	
	//删除一条记录 通常为软删
	void deleteInventory(Inventory inventory);

	//选取所有记录
	List<Inventory> selectAll();

	//分页查询数据在父类

	//更新单条记录
	void ensureInventory(Inventory inventory);
}
