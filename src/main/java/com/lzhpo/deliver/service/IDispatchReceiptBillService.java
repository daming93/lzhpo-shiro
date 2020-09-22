package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.DispatchReceiptBill;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
public interface IDispatchReceiptBillService extends IService<DispatchReceiptBill> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDispatchReceiptBillCount(String name);
	
	//保存实例 返回该实例
	DispatchReceiptBill saveDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill);

	//根据实例Id获取实例
	DispatchReceiptBill getDispatchReceiptBillById(String id);

	//更新单条记录
	void updateDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill);
	
	//删除一条记录 通常为软删
	void deleteDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill);

	//选取所有记录
	List<DispatchReceiptBill> selectAll();

	//分页查询数据在父类


}
