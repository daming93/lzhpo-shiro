package com.lzhpo.stock.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.StorageDetail;
/**
 * <p>
 * 入库明细表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface IStorageDetailService extends IService<StorageDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getStorageDetailCount(String name);
	
	//保存实例 返回该实例
	StorageDetail saveStorageDetail(StorageDetail storageDetail);

	//根据实例Id获取实例
	StorageDetail getStorageDetailById(String id);

	//更新单条记录
	void updateStorageDetail(StorageDetail storageDetail);
	
	//删除一条记录 通常为软删
	void deleteStorageDetail(StorageDetail storageDetail);

	//选取所有记录
	List<StorageDetail> selectAll();

	//分页查询数据在父类

	//根据主表id删除子表 硬删
	void deleteStorageDetailById(String storageId);
	
	//选取所有记录
	List<StorageDetail> selectStorageDetailByStorageId(String storageId);
	
	Set<String> selectStorageIdsByMaterialId(String materialId);
}
