package com.lzhpo.warehouse.service;

import com.lzhpo.warehouse.entity.ChangeMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 调仓物料表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-03
 */
public interface IChangeMaterialService extends IService<ChangeMaterial> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getChangeMaterialCount(String name);
	
	//保存实例 返回该实例
	ChangeMaterial saveChangeMaterial(ChangeMaterial changeMaterial);

	//根据实例Id获取实例
	ChangeMaterial getChangeMaterialById(String id);

	//更新单条记录
	void updateChangeMaterial(ChangeMaterial changeMaterial);
	
	//删除一条记录 通常为软删
	void deleteChangeMaterial(ChangeMaterial changeMaterial);

	//选取所有记录
	List<ChangeMaterial> selectAll();

	//分页查询数据在父类


}
