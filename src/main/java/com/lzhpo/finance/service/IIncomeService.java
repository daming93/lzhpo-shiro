package com.lzhpo.finance.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.finance.entity.Income;
import com.lzhpo.stock.entity.DirectReturn;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.entity.SaleReturn;
import com.lzhpo.stock.entity.Storage;
import com.lzhpo.stock.entity.Takeout;
/**
 * <p>
 * 财务收入 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-04-14
 */
public interface IIncomeService extends IService<Income> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getIncomeCount(String name);
	
	//保存实例 返回该实例
	Income saveIncome(Income income);

	//根据实例Id获取实例
	Income getIncomeById(String id);

	//更新单条记录
	void updateIncome(Income income);
	
	//删除一条记录 通常为软删
	void deleteIncome(Income income);

	//选取所有记录
	List<Income> selectAll();

	//分页查询数据在父类

	//出库装卸费计算
	Takeout takeoutIncomeMath(Takeout takeout) throws Exception;
	
	//出库装卸费计算 线路得出库装卸费 在出库单得时候直接生成
	LineTakeout linetakeoutIncomeMath(LineTakeout takeout) throws Exception;
	
	Storage storageIncomeMath(Storage storage)throws Exception;
	
	SaleReturn saleReturnIncomeMath(SaleReturn saleReturn)throws Exception;
	
	DirectReturn directReturnIncomeMath(DirectReturn directReturn)throws Exception;
	
	void deleteByWaybillId(String wayBillId);
}
