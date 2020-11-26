package com.lzhpo.customer.service;

import com.lzhpo.customer.entity.ServiceExcptionreport;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;
/**
 * <p>
 * 异常日报 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
public interface IServiceExcptionreportService extends IService<ServiceExcptionreport> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getServiceExcptionreportCount(String name);
	
	//保存实例 返回该实例
	ServiceExcptionreport saveServiceExcptionreport(ServiceExcptionreport serviceExcptionreport);

	//根据实例Id获取实例
	ServiceExcptionreport getServiceExcptionreportById(String id);

	//更新单条记录
	void updateServiceExcptionreport(ServiceExcptionreport serviceExcptionreport);
	
	//删除一条记录 通常为软删
	void deleteServiceExcptionreport(ServiceExcptionreport serviceExcptionreport);

	//选取所有记录
	List<ServiceExcptionreport> selectAll();

	//分页查询数据在父类


}
