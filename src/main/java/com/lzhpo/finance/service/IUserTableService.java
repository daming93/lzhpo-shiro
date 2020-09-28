package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.UserTable;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 自定义收入表--用户使用（在不同表格中不同体现) 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
public interface IUserTableService extends IService<UserTable> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getUserTableCount(String name);
	
	//保存实例 返回该实例
	UserTable saveUserTable(UserTable userTable);

	//根据实例Id获取实例
	UserTable getUserTableById(String id);

	//更新单条记录
	void updateUserTable(UserTable userTable);
	
	//删除一条记录 通常为软删
	void deleteUserTable(UserTable userTable);

	//选取所有记录
	List<UserTable> selectAll();

	//分页查询数据在父类

	//根据实例userTableId获取实例
	UserTable getUserTableByuserTableId(String userTableId);	
}
