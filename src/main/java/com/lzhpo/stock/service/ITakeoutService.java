package com.lzhpo.stock.service;

import java.util.List;
import java.util.Set;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.stock.entity.Takeout;
/**
 * <p>
 * 出库表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
public interface ITakeoutService extends IService<Takeout> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTakeoutCount(String name);
	
	//保存实例 返回该实例
	Takeout saveTakeout(Takeout takeout);

	//根据实例Id获取实例
	Takeout getTakeoutById(String id);

	//更新单条记录
	void updateTakeout(Takeout takeout);
	
	//删除一条记录 通常为软删
	void deleteTakeout(Takeout takeout);

	//选取所有记录
	List<Takeout> selectAll();

	void backTakeout(String id);

	//分页查询数据在父类

	void ensurePick(String id);
	
	List<Takeout> selectAllByDispatchIds(Set<String> dispatchIds);
}
