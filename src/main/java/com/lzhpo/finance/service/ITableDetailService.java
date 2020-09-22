package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.TableDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 自定义收入表明细 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
public interface ITableDetailService extends IService<TableDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTableDetailCount(String name);
	
	//保存实例 返回该实例
	TableDetail saveTableDetail(TableDetail tableDetail);

	//根据实例Id获取实例
	TableDetail getTableDetailById(String id);

	//更新单条记录
	void updateTableDetail(TableDetail tableDetail);
	
	//删除一条记录 通常为软删
	void deleteTableDetail(TableDetail tableDetail);

	//选取所有记录
	List<TableDetail> selectAll();

	//分页查询数据在父类


}
