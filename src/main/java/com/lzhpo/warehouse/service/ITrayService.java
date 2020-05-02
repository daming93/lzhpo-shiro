package com.lzhpo.warehouse.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.warehouse.entity.Tray;
/**
 * <p>
 * 储位表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-24
 */
public interface ITrayService extends IService<Tray> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTrayCount(Tray tray);
	
	//保存实例 返回该实例
	Tray saveTray(Tray tray);

	//根据实例Id获取实例
	Tray getTrayById(String id);

	//更新单条记录
	void updateTray(Tray tray);
	
	//删除一条记录 通常为软删
	void deleteTray(Tray tray);

	//选取所有记录
	List<Tray> selectAll();

	//分页查询数据在父类

	//验证code是否符合标准
	String judCode(Tray tray);
}
