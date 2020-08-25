package com.lzhpo.sys.service.impl;

import com.lzhpo.sys.entity.Territory;
import com.lzhpo.sys.mapper.TerritoryMapper;
import com.lzhpo.sys.service.ITerritoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 区域表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-07
 */
@Service
public class TerritoryServiceImpl extends ServiceImpl<TerritoryMapper, Territory> implements ITerritoryService {
	@Override
    public long getTerritoryCount(String name) {
        QueryWrapper<Territory> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Territorys", allEntries = true)
    public Territory saveTerritory(Territory territory) {
        baseMapper.insert(territory);
        /**
	*预留编辑代码 
	*/
        return territory;
    }

    @Override
    public Territory getTerritoryById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Territorys", allEntries = true)
    public void updateTerritory(Territory territory) {
        baseMapper.updateById(territory);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Territorys", allEntries = true)
    public void deleteTerritory(Territory territory) {
        territory.setDelFlag(true);
        baseMapper.updateById(territory);
    }

    @Override
    @Cacheable("Territorys")
    public List<Territory> selectAll() {
        QueryWrapper<Territory> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
