package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.TableOption;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 自定义收入表选项 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-21
 */
public interface ITableOptionService extends IService<TableOption> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getTableOptionCount(String name);
	
	//保存实例 返回该实例
	TableOption saveTableOption(TableOption tableOption);

	//根据实例Id获取实例
	TableOption getTableOptionById(String id);

	//更新单条记录
	void updateTableOption(TableOption tableOption);
	
	//删除一条记录 通常为软删
	void deleteTableOption(TableOption tableOption);

	//选取所有记录
	List<TableOption> selectAll();

	//分页查询数据在父类


}
