package com.lzhpo.warehouse.service;

import com.lzhpo.warehouse.entity.TakeoutExcption;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 物料品牌表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-06
 */
public interface ITakeoutExcptionService extends IService<TakeoutExcption> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTakeoutExcptionCount(String name);
	
	//保存实例 返回该实例
	TakeoutExcption saveTakeoutExcption(TakeoutExcption takeoutExcption);

	//根据实例Id获取实例
	TakeoutExcption getTakeoutExcptionById(String id);

	//更新单条记录
	void updateTakeoutExcption(TakeoutExcption takeoutExcption);
	
	//删除一条记录 通常为软删
	void deleteTakeoutExcption(TakeoutExcption takeoutExcption);

	//选取所有记录
	List<TakeoutExcption> selectAll();

	//分页查询数据在父类


}
