package com.lzhpo.deliver.service.impl;

import com.lzhpo.deliver.entity.Driver;
import com.lzhpo.deliver.mapper.DriverMapper;
import com.lzhpo.deliver.service.IDriverService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 驾驶员信息表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-14
 */
@Service
public class DriverServiceImpl extends ServiceImpl<DriverMapper, Driver> implements IDriverService {
	@Override
    public long getDriverCount(String name) {
        QueryWrapper<Driver> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Drivers", allEntries = true)
    public Driver saveDriver(Driver driver) {
        baseMapper.insert(driver);
        /**
	*预留编辑代码 
	*/
        return driver;
    }

    @Override
    public Driver getDriverById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Drivers", allEntries = true)
    public void updateDriver(Driver driver) {
        baseMapper.updateById(driver);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Drivers", allEntries = true)
    public void deleteDriver(Driver driver) {
        driver.setDelFlag(true);
        baseMapper.updateById(driver);
    }

    @Override
    @Cacheable("Drivers")
    public List<Driver> selectAll() {
        QueryWrapper<Driver> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
