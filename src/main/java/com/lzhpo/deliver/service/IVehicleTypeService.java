package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.VehicleType;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-20
 */
public interface IVehicleTypeService extends IService<VehicleType> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getVehicleTypeCount(String name);
	
	//保存实例 返回该实例
	VehicleType saveVehicleType(VehicleType vehicleType);

	//根据实例Id获取实例
	VehicleType getVehicleTypeById(String id);

	//更新单条记录
	void updateVehicleType(VehicleType vehicleType);
	
	//删除一条记录 通常为软删
	void deleteVehicleType(VehicleType vehicleType);

	//选取所有记录
	List<VehicleType> selectAll();

	//分页查询数据在父类


}
