package com.lzhpo.warehouse.service;

import com.lzhpo.warehouse.entity.MaterialManage;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
public interface IMaterialManageService extends IService<MaterialManage> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getMaterialManageCount(String name);
	
	//保存实例 返回该实例
	MaterialManage saveMaterialManage(MaterialManage materialManage);

	//根据实例Id获取实例
	MaterialManage getMaterialManageById(String id);

	//更新单条记录
	void updateMaterialManage(MaterialManage materialManage);
	
	//删除一条记录 通常为软删
	void deleteMaterialManage(MaterialManage materialManage);

	//选取所有记录
	List<MaterialManage> selectAll();

	//分页查询数据在父类


}
