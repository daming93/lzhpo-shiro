package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.SaleReturnOperations;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 出库操作表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface ISaleReturnOperationsService extends IService<SaleReturnOperations> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getSaleReturnOperationsCount(String name);
	
	//保存实例 返回该实例
	SaleReturnOperations saveSaleReturnOperations(SaleReturnOperations saleReturnOperations);

	//根据实例Id获取实例
	SaleReturnOperations getSaleReturnOperationsById(String id);

	//更新单条记录
	void updateSaleReturnOperations(SaleReturnOperations saleReturnOperations);
	
	//删除一条记录 通常为软删
	void deleteSaleReturnOperations(SaleReturnOperations saleReturnOperations);

	//选取所有记录
	List<SaleReturnOperations> selectAll();

	//分页查询数据在父类
	List<SaleReturnOperations> selectByReturnId(String returnId);

}
