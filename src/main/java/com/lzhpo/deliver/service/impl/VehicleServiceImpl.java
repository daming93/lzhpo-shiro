package com.lzhpo.deliver.service.impl;

import com.lzhpo.deliver.entity.Vehicle;
import com.lzhpo.deliver.mapper.VehicleMapper;
import com.lzhpo.deliver.service.IVehicleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 车辆信息表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-21
 */
@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements IVehicleService {
	@Override
    public long getVehicleCount(String licence_plate) {
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("licence_plate",licence_plate);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Vehicles", allEntries = true)
    public Vehicle saveVehicle(Vehicle vehicle) {
        baseMapper.insert(vehicle);
        /**
	*预留编辑代码 
	*/
        return vehicle;
    }

    @Override
    public Vehicle getVehicleById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Vehicles", allEntries = true)
    public void updateVehicle(Vehicle vehicle) {
        baseMapper.updateById(vehicle);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Vehicles", allEntries = true)
    public void deleteVehicle(Vehicle vehicle) {
        vehicle.setDelFlag(true);
        baseMapper.updateById(vehicle);
    }

    @Override
    @Cacheable("Vehicles")
    public List<Vehicle> selectAll() {
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
