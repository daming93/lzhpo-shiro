package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.HandleAbnormity;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 异常处理表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-12-02
 */
public interface IHandleAbnormityService extends IService<HandleAbnormity> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getHandleAbnormityCount(String tableId);
	
	//保存实例 返回该实例
	HandleAbnormity saveHandleAbnormity(HandleAbnormity handleAbnormity);

	//根据实例Id获取实例
	HandleAbnormity getHandleAbnormityById(String id);

	//更新单条记录
	void updateHandleAbnormity(HandleAbnormity handleAbnormity);
	
	//删除一条记录 通常为软删
	void deleteHandleAbnormity(HandleAbnormity handleAbnormity);

	//选取所有记录
	List<HandleAbnormity> selectAll();

	//分页查询数据在父类

	//撤销审核
	void backAudit(String id);

	HandleAbnormity getHandleAbnormityByTakeoutId(String tableId);
	
	//获取满足某个条件的记录 以便不插入重复记录
	long getHandleAbnormityCountBywaybillId(String waybillId);
	
	HandleAbnormity changeStatusByIdAndStatus(String id,Integer status);
}
