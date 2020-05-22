package com.lzhpo.stock.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.MaterialOperations;
/**
 * <p>
 * 仓储明细表-操作表可以相当于流水表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
public interface IMaterialOperationsService extends IService<MaterialOperations> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialOperationsCount(String name);
	
	//保存实例 返回该实例
	MaterialOperations saveMaterialOperations(MaterialOperations materialOperations);

	//根据实例Id获取实例
	MaterialOperations getMaterialOperationsById(String id);

	//更新单条记录
	void updateMaterialOperations(MaterialOperations materialOperations);
	
	//删除一条记录 通常为软删
	void deleteMaterialOperations(MaterialOperations materialOperations);

	//选取所有记录
	List<MaterialOperations> selectAll();

	//分页查询数据在父类
	
	
	List<MaterialOperations> selectByMaterialId(String materialId);
	
	
	//物料流水 两种模式一种是忽略明细 一种是忽略批次
	/**
	 * flag 是是否忽略批次
	 * @param itemName
	 * @param startTime
	 * @param endTime
	 * @param flag
	 * @return
	 */
	Map<String,Object> selectOperations(String itemName,String startTime,String endTime,Integer start,Integer limit,Integer flag);
	
	
}
