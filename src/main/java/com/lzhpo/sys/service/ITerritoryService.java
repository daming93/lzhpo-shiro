package com.lzhpo.sys.service;

import com.lzhpo.sys.entity.Territory;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 区域表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
public interface ITerritoryService extends IService<Territory> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTerritoryCount(String name);
	
	//保存实例 返回该实例
	Territory saveTerritory(Territory territory);

	//根据实例Id获取实例
	Territory getTerritoryById(String id);

	//更新单条记录
	void updateTerritory(Territory territory);
	
	//删除一条记录 通常为软删
	void deleteTerritory(Territory territory);

	//选取所有记录
	List<Territory> selectAll();

	//分页查询数据在父类


}
