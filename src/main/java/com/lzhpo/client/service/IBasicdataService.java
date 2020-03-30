package com.lzhpo.client.service;

import com.lzhpo.client.entity.Basicdata;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
/**
 * <p>
 * 客户表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-03-24
 */
public interface IBasicdataService extends IService<Basicdata> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getBasicdataCount(String name);
	
	//保存实例 返回该实例
	Basicdata saveBasicdata(Basicdata basicdata);

	//根据实例Id获取实例
	Basicdata getBasicdataById(String id);

	//更新单条记录
	void updateBasicdata(Basicdata basicdata);
	
	//删除一条记录 通常为软删
	void deleteBasicdata(Basicdata basicdata);

	//选取所有记录
	List<Basicdata> selectAll();

	//分页查询数据在父类
	
}	
