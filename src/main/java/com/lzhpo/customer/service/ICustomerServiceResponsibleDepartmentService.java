package com.lzhpo.customer.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lzhpo.customer.entity.CustomerServiceResponsibleDepartment;
/**
 * <p>
 * 客服责任部门名 服务类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
public interface ICustomerServiceResponsibleDepartmentService extends IService<CustomerServiceResponsibleDepartment> {
	//获取满足某个条件的记录 以便不插入重复记录
	long getCustomerServiceResponsibleDepartmentCount(String name);
	
	//保存实例 返回该实例
	CustomerServiceResponsibleDepartment saveCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment);

	//根据实例Id获取实例
	CustomerServiceResponsibleDepartment getCustomerServiceResponsibleDepartmentById(String id);

	//更新单条记录
	void updateCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment);
	
	//删除一条记录 通常为软删
	void deleteCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment);

	//选取所有记录
	List<CustomerServiceResponsibleDepartment> selectAll();

	//分页查询数据在父类


}
