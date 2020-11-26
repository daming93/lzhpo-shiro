package com.lzhpo.customer.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.customer.entity.CustomerServiceResponsibleDepartment;
import com.lzhpo.customer.mapper.CustomerServiceResponsibleDepartmentMapper;
import com.lzhpo.customer.service.ICustomerServiceResponsibleDepartmentService;
/**
 * <p>
 * 客服责任部门名 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Service
public class CustomerServiceResponsibleDepartmentServiceImpl extends ServiceImpl<CustomerServiceResponsibleDepartmentMapper, CustomerServiceResponsibleDepartment> implements ICustomerServiceResponsibleDepartmentService {
	@Override
    public long getCustomerServiceResponsibleDepartmentCount(String name) {
        QueryWrapper<CustomerServiceResponsibleDepartment> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "CustomerServiceResponsibleDepartments", allEntries = true)
    public CustomerServiceResponsibleDepartment saveCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment) {
        baseMapper.insert(customerServiceResponsibleDepartment);
        /**
	*预留编辑代码 
	*/
        return customerServiceResponsibleDepartment;
    }

    @Override
    public CustomerServiceResponsibleDepartment getCustomerServiceResponsibleDepartmentById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "CustomerServiceResponsibleDepartments", allEntries = true)
    public void updateCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment) {
        baseMapper.updateById(customerServiceResponsibleDepartment);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "CustomerServiceResponsibleDepartments", allEntries = true)
    public void deleteCustomerServiceResponsibleDepartment(CustomerServiceResponsibleDepartment customerServiceResponsibleDepartment) {
        customerServiceResponsibleDepartment.setDelFlag(true);
        baseMapper.updateById(customerServiceResponsibleDepartment);
    }

    @Override
    @Cacheable("CustomerServiceResponsibleDepartments")
    public List<CustomerServiceResponsibleDepartment> selectAll() {
        QueryWrapper<CustomerServiceResponsibleDepartment> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
