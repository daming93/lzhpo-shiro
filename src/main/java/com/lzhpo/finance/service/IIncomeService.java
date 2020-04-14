package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.Income;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
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


}
