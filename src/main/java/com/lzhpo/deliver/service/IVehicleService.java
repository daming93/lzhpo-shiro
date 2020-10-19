package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.Vehicle;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 车辆信息表 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-21
 */
public interface IVehicleService extends IService<Vehicle> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getVehicleCount(String name);
	
	//保存实例 返回该实例
	Vehicle saveVehicle(Vehicle vehicle);

	//根据实例Id获取实例
	Vehicle getVehicleById(String id);

	//更新单条记录
	void updateVehicle(Vehicle vehicle);
	
	//删除一条记录 通常为软删
	void deleteVehicle(Vehicle vehicle);

	//选取所有记录
	List<Vehicle> selectAll();

	//分页查询数据在父类
	

}
