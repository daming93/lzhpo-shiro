package com.lzhpo.customer.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.customer.entity.Abnormity;
/**
 * <p>
 * 异常表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface IAbnormityService extends IService<Abnormity> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getAbnormityCount(String name);
	
	//保存实例 返回该实例
	Abnormity saveAbnormity(Abnormity abnormity);

	//根据实例Id获取实例
	Abnormity getAbnormityById(String id);

	//更新单条记录
	void updateAbnormity(Abnormity abnormity);
	
	//删除一条记录 通常为软删
	void deleteAbnormity(Abnormity abnormity);

	//选取所有记录
	List<Abnormity> selectAll();

	//分页查询数据在父类

	List<Abnormity> selectAbnorityByType(String typeId);
}
