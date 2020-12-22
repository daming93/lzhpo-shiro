package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.DirectReturn;
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
public interface IDirectReturnService extends IService<DirectReturn> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDirectReturnCount(String name);
	
	//保存实例 返回该实例
	DirectReturn saveDirectReturn(DirectReturn DirectReturn);

	//根据实例Id获取实例
	DirectReturn getDirectReturnById(String id);

	//更新单条记录
	void updateDirectReturn(DirectReturn DirectReturn);
	
	//删除一条记录 通常为软删
	void deleteDirectReturn(DirectReturn DirectReturn);

	//选取所有记录
	List<DirectReturn> selectAll();

	//分页查询数据在父类

	//撤回退货单
	void backDirectReturn(String DirectReturnId);
	
	//锁住单据 如果出库用了某单的物品
	
	void lockDirectReturn(String materialId);
}
