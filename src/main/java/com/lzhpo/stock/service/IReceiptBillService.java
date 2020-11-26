package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.ReceiptBill;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-11-23
 */
public interface IReceiptBillService extends IService<ReceiptBill> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getReceiptBillCount(String name);
	
	//保存实例 返回该实例
	ReceiptBill saveReceiptBill(ReceiptBill receiptBill);

	//根据实例Id获取实例
	ReceiptBill getReceiptBillById(String id);

	//更新单条记录
	void updateReceiptBill(ReceiptBill receiptBill);
	
	//删除一条记录 通常为软删
	void deleteReceiptBill(ReceiptBill receiptBill);

	//选取所有记录
	List<ReceiptBill> selectAll();

	//分页查询数据在父类

	void deleteReceiptBillByTakeoutId(String takeoutId);
	
	//根据实例Id获取实例
	ReceiptBill getReceiptBillByTakeoutId(String takeoutId);
	
	void changeStatusByTakeoutId(String takeoutId,Integer receiptStatus);
	
	void changeStatusById(String id,Integer receiptStatus);
}
