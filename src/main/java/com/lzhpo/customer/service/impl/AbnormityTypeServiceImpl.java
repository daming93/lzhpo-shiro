package com.lzhpo.customer.service.impl;

import com.lzhpo.customer.entity.AbnormityType;
import com.lzhpo.customer.mapper.AbnormityTypeMapper;
import com.lzhpo.customer.service.IAbnormityTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 异常类型 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-11-17
 */
@Service
public class AbnormityTypeServiceImpl extends ServiceImpl<AbnormityTypeMapper, AbnormityType> implements IAbnormityTypeService {
	@Override
    public long getAbnormityTypeCount(String name) {
        QueryWrapper<AbnormityType> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "AbnormityTypes", allEntries = true)
    public AbnormityType saveAbnormityType(AbnormityType abnormityType) {
        baseMapper.insert(abnormityType);
        /**
	*预留编辑代码 
	*/
        return abnormityType;
    }

    @Override
    public AbnormityType getAbnormityTypeById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "AbnormityTypes", allEntries = true)
    public void updateAbnormityType(AbnormityType abnormityType) {
        baseMapper.updateById(abnormityType);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "AbnormityTypes", allEntries = true)
    public void deleteAbnormityType(AbnormityType abnormityType) {
        abnormityType.setDelFlag(true);
        baseMapper.updateById(abnormityType);
    }

    @Override
    @Cacheable("AbnormityTypes")
    public List<AbnormityType> selectAll() {
        QueryWrapper<AbnormityType> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
