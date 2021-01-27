package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.LineTakeout;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 线路发单（无关库存的发单) 表单形式和库存发单接近，但是没有子表 服务类
 * </p>
 *
 * @author xdm
 * @since 2021-01-25
 */
public interface ILineTakeoutService extends IService<LineTakeout> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getLineTakeoutCount(String name);
	
	//保存实例 返回该实例
	LineTakeout saveLineTakeout(LineTakeout lineTakeout);

	//根据实例Id获取实例
	LineTakeout getLineTakeoutById(String id);

	//更新单条记录
	void updateLineTakeout(LineTakeout lineTakeout);
	
	//删除一条记录 通常为软删
	void deleteLineTakeout(LineTakeout lineTakeout);

	//选取所有记录
	List<LineTakeout> selectAll();

	//分页查询数据在父类

	void backTakeout(String id);
}
