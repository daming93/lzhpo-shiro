package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.SaleReturn;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 退货表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface ISaleReturnService extends IService<SaleReturn> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getSaleReturnCount(String name);
	
	//保存实例 返回该实例
	SaleReturn saveSaleReturn(SaleReturn saleReturn);

	//根据实例Id获取实例
	SaleReturn getSaleReturnById(String id);

	//更新单条记录
	void updateSaleReturn(SaleReturn saleReturn);
	
	//删除一条记录 通常为软删
	void deleteSaleReturn(SaleReturn saleReturn);

	//选取所有记录
	List<SaleReturn> selectAll();

	//分页查询数据在父类


}
