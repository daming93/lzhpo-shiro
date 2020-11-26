package com.lzhpo.customer.service.impl;

import com.lzhpo.customer.entity.ServiceExcptionreport;
import com.lzhpo.customer.mapper.ServiceExcptionreportMapper;
import com.lzhpo.customer.service.IServiceExcptionreportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 异常日报 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Service
public class ServiceExcptionreportServiceImpl extends ServiceImpl<ServiceExcptionreportMapper, ServiceExcptionreport> implements IServiceExcptionreportService {
	@Override
    public long getServiceExcptionreportCount(String name) {
        QueryWrapper<ServiceExcptionreport> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ServiceExcptionreports", allEntries = true)
    public ServiceExcptionreport saveServiceExcptionreport(ServiceExcptionreport serviceExcptionreport) {
        baseMapper.insert(serviceExcptionreport);
        /**
	*预留编辑代码 
	*/
        return serviceExcptionreport;
    }

    @Override
    public ServiceExcptionreport getServiceExcptionreportById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ServiceExcptionreports", allEntries = true)
    public void updateServiceExcptionreport(ServiceExcptionreport serviceExcptionreport) {
        baseMapper.updateById(serviceExcptionreport);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ServiceExcptionreports", allEntries = true)
    public void deleteServiceExcptionreport(ServiceExcptionreport serviceExcptionreport) {
        serviceExcptionreport.setDelFlag(true);
        baseMapper.updateById(serviceExcptionreport);
    }

    @Override
    @Cacheable("ServiceExcptionreports")
    public List<ServiceExcptionreport> selectAll() {
        QueryWrapper<ServiceExcptionreport> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
