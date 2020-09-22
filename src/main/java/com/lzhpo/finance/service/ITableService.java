package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.Table;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 自定义收入表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
public interface ITableService extends IService<Table> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTableCount(String name);
	
	//保存实例 返回该实例
	Table saveTable(Table table);

	//根据实例Id获取实例
	Table getTableById(String id);

	//更新单条记录
	void updateTable(Table table);
	
	//删除一条记录 通常为软删
	void deleteTable(Table table);

	//选取所有记录
	List<Table> selectAll();

	void ChangeAduitStatus(Integer status, String id);

	//分页查询数据在父类


}
