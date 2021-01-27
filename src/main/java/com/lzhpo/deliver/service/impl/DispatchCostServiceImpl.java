package com.lzhpo.deliver.service.impl;

import com.lzhpo.deliver.entity.DispatchCost;
import com.lzhpo.deliver.mapper.DispatchCostMapper;
import com.lzhpo.deliver.service.IDispatchCostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 配送计划支出表 司机运费 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2021-01-22
 */
@Service
public class DispatchCostServiceImpl extends ServiceImpl<DispatchCostMapper, DispatchCost> implements IDispatchCostService {
	@Override
    public long getDispatchCostCount(String name) {
        QueryWrapper<DispatchCost> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchCosts", allEntries = true)
    public DispatchCost saveDispatchCost(DispatchCost dispatchCost) {
        baseMapper.insert(dispatchCost);
        /**
	*预留编辑代码 
	*/
        return dispatchCost;
    }

    @Override
    public DispatchCost getDispatchCostById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchCosts", allEntries = true)
    public void updateDispatchCost(DispatchCost dispatchCost) {
        baseMapper.updateById(dispatchCost);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchCosts", allEntries = true)
    public void deleteDispatchCost(DispatchCost dispatchCost) {
        dispatchCost.setDelFlag(true);
        baseMapper.updateById(dispatchCost);
    }

    @Override
    @Cacheable("DispatchCosts")
    public List<DispatchCost> selectAll() {
        QueryWrapper<DispatchCost> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void deleteByDispatchId(String dispatchId) {
		QueryWrapper<DispatchCost> wrapper = new QueryWrapper<>();
        wrapper.eq("dispatch_id",dispatchId);
        baseMapper.delete(wrapper);
	}


}
