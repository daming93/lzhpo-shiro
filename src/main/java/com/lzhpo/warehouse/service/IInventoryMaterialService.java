package com.lzhpo.warehouse.service;

import com.lzhpo.warehouse.entity.InventoryMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 盘点物料表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
public interface IInventoryMaterialService extends IService<InventoryMaterial> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getInventoryMaterialCount(String name);
	
	//保存实例 返回该实例
	InventoryMaterial saveInventoryMaterial(InventoryMaterial inventoryMaterial);

	//根据实例Id获取实例
	InventoryMaterial getInventoryMaterialById(String id);

	//更新单条记录
	void updateInventoryMaterial(InventoryMaterial inventoryMaterial);
	
	//删除一条记录 通常为软删
	void deleteInventoryMaterial(InventoryMaterial inventoryMaterial);

	//选取所有记录
	List<InventoryMaterial> selectAll();

	//分页查询数据在父类
	
	//提供主表id,返回子表中是否有盘点差异量
	
	long selectDetailDiffenent(String inventoryId);
}
