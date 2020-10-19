package com.lzhpo.deliver.service;

import com.lzhpo.deliver.entity.VehicleContractMainDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;
/**
 * <p>
 * 合同收费项 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
public interface IVehicleContractMainDetailService extends IService<VehicleContractMainDetail> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getVehicleContractMainDetailCount(String name);
	
	//保存实例 返回该实例
	VehicleContractMainDetail saveVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail);

	//根据实例Id获取实例
	VehicleContractMainDetail getVehicleContractMainDetailById(String id);

	//更新单条记录
	void updateVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail);
	
	//删除一条记录 通常为软删
	void deleteVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail);

	//选取所有记录
	List<VehicleContractMainDetail> selectAll();

	//分页查询数据在父类

	//给主表id找子表
	List<VehicleContractMainDetail> getListByMainId(String mainId);
	
	void deleteAllDetailByMainId(String mainId);
	
	//根据主合同查询最贵的那条金额
	BigDecimal selectDetailMoneyByInfo(String mainId,String proviceId,String cityId,String areaId,Integer range);

	//根据主合同查询是否有该区域的报价
	VehicleContractMainDetail selectDetailMoneyByInfoNoRange(String mainId,String proviceId,String cityId,String areaId);
}
