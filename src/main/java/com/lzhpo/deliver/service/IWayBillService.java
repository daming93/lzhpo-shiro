package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.WayBill;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 录单(和计划表基本一样，是计划表得主表，和统计内容) 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-10-14
 */
public interface IWayBillService extends IService<WayBill> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getWayBillCount(String name);
	
	//保存实例 返回该实例
	WayBill saveWayBill( List<Dispatch> dispatchs);
	
	int verifyWayBill( List<Dispatch> dispatchs);

	//根据实例Id获取实例
	WayBill getWayBillById(String id);

	//更新单条记录
	void updateWayBill(WayBill wayBill);
	
	//删除一条记录 通常为软删
	void deleteWayBill(WayBill wayBill);

	//选取所有记录
	List<WayBill> selectAll();

	//分页查询数据在父类
	

}
