package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.Driver;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 驾驶员信息表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-14
 */
public interface IDriverService extends IService<Driver> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getDriverCount(String name);
	
	//保存实例 返回该实例
	Driver saveDriver(Driver driver);

	//根据实例Id获取实例
	Driver getDriverById(String id);

	//更新单条记录
	void updateDriver(Driver driver);
	
	//删除一条记录 通常为软删
	void deleteDriver(Driver driver);

	//选取所有记录
	List<Driver> selectAll();

	//分页查询数据在父类


}
