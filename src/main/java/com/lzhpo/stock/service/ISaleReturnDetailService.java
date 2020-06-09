package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.SaleReturnDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 退货详情表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
public interface ISaleReturnDetailService extends IService<SaleReturnDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getSaleReturnDetailCount(String name);
	
	//保存实例 返回该实例
	SaleReturnDetail saveSaleReturnDetail(SaleReturnDetail saleReturnDetail);

	//根据实例Id获取实例
	SaleReturnDetail getSaleReturnDetailById(String id);

	//更新单条记录
	void updateSaleReturnDetail(SaleReturnDetail saleReturnDetail);
	
	//删除一条记录 通常为软删
	void deleteSaleReturnDetail(SaleReturnDetail saleReturnDetail);

	//选取所有记录
	List<SaleReturnDetail> selectAll();

	//分页查询数据在父类


}
