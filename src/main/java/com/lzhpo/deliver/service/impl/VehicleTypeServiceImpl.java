package com.lzhpo.deliver.service.impl;

import com.lzhpo.deliver.entity.VehicleType;
import com.lzhpo.deliver.mapper.VehicleTypeMapper;
import com.lzhpo.deliver.service.IVehicleTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-20
 */
@Service
public class VehicleTypeServiceImpl extends ServiceImpl<VehicleTypeMapper, VehicleType> implements IVehicleTypeService {
	@Override
    public long getVehicleTypeCount(String name) {
        QueryWrapper<VehicleType> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleTypes", allEntries = true)
    public VehicleType saveVehicleType(VehicleType vehicleType) {
        baseMapper.insert(vehicleType);
        /**
	*预留编辑代码 
	*/
        return vehicleType;
    }

    @Override
    public VehicleType getVehicleTypeById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleTypes", allEntries = true)
    public void updateVehicleType(VehicleType vehicleType) {
        baseMapper.updateById(vehicleType);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleTypes", allEntries = true)
    public void deleteVehicleType(VehicleType vehicleType) {
        vehicleType.setDelFlag(true);
        baseMapper.updateById(vehicleType);
    }

    @Override
    @Cacheable("VehicleTypes")
    public List<VehicleType> selectAll() {
        QueryWrapper<VehicleType> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
