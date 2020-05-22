package com.lzhpo.stock.service;

import com.lzhpo.stock.entity.TakeoutDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
public interface ITakeoutDetailService extends IService<TakeoutDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTakeoutDetailCount(String name);
	
	//保存实例 返回该实例
	TakeoutDetail saveTakeoutDetail(TakeoutDetail takeoutDetail);

	//根据实例Id获取实例
	TakeoutDetail getTakeoutDetailById(String id);

	//更新单条记录
	void updateTakeoutDetail(TakeoutDetail takeoutDetail);
	
	//删除一条记录 通常为软删
	void deleteTakeoutDetail(TakeoutDetail takeoutDetail,String code);

	//选取所有记录
	List<TakeoutDetail> selectAll();

	//分页查询数据在父类

	//修改单独列得出库明细
	void updateNumber(TakeoutDetail takeoutDetail,String code) throws Exception;

	List<TakeoutDetail> selecttakeoutDetailBytakeoutId(String id);
	
}
