package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.ExpressBill;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 配送零单配送 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-12
 */
public interface IExpressBillService extends IService<ExpressBill> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getExpressBillCount(String name);
	
	//保存实例 返回该实例
	ExpressBill saveExpressBill(ExpressBill expressBill);

	//根据实例Id获取实例
	ExpressBill getExpressBillById(String id);

	//更新单条记录
	void updateExpressBill(ExpressBill expressBill);
	
	//删除一条记录 通常为软删
	void deleteExpressBill(ExpressBill expressBill);

	//选取所有记录
	List<ExpressBill> selectAll();

	//分页查询数据在父类
	public ExpressBill backExpressBill(String id);
	
	public ExpressBill getSendPeopelInfoBySendPhone(String sendPhone);
	
	public ExpressBill getReceivePeopelInfoByReceivePhone(String receivePhone);
}
