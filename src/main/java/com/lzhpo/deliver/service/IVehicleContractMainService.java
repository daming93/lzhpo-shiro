package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.VehicleContractMain;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
public interface IVehicleContractMainService extends IService<VehicleContractMain> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getVehicleContractMainCount(String name);
	
	//保存实例 返回该实例
	VehicleContractMain saveVehicleContractMain(VehicleContractMain vehicleContractMain);

	//根据实例Id获取实例
	VehicleContractMain getVehicleContractMainById(String id);

	//更新单条记录
	void updateVehicleContractMain(VehicleContractMain vehicleContractMain);
	
	//删除一条记录 通常为软删
	void deleteVehicleContractMain(VehicleContractMain vehicleContractMain);

	//选取所有记录
	List<VehicleContractMain> selectAll();

	//分页查询数据在父类

	//复制合同 改变合同的名字 变成副本 合同编号 主表子表同步复制
	void copyContract(String contractId);
	

}
