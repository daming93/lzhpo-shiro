package com.lzhpo.finance.service;

import com.lzhpo.finance.entity.UserTableDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 自定义收入表明细--用户使用（在不同表格中不同体现) 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-09-25
 */
public interface IUserTableDetailService extends IService<UserTableDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getUserTableDetailCount(String name);
	
	//保存实例 返回该实例
	UserTableDetail saveUserTableDetail(UserTableDetail userTableDetail);

	//根据实例Id获取实例
	UserTableDetail getUserTableDetailById(String id);

	//更新单条记录
	void updateUserTableDetail(UserTableDetail userTableDetail);
	
	//删除一条记录 通常为软删
	void deleteUserTableDetail(UserTableDetail userTableDetail);

	//选取所有记录
	List<UserTableDetail> selectAll();

	//分页查询数据在父类


}
