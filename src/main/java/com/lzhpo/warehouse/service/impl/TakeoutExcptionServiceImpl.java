package com.lzhpo.warehouse.service.impl;

import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.warehouse.entity.TakeoutExcption;
import com.lzhpo.warehouse.mapper.TakeoutExcptionMapper;
import com.lzhpo.warehouse.service.ITakeoutExcptionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 物料品牌表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-06
 */
@Service
public class TakeoutExcptionServiceImpl extends ServiceImpl<TakeoutExcptionMapper, TakeoutExcption> implements ITakeoutExcptionService {
	@Autowired
	private ITakeoutService takeoutService;
	
	@Override
    public long getTakeoutExcptionCount(String name) {
        QueryWrapper<TakeoutExcption> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutExcptions", allEntries = true)
    public TakeoutExcption saveTakeoutExcption(TakeoutExcption takeoutExcption) {
        baseMapper.insert(takeoutExcption);
        /**
	*预留编辑代码 
	*/
        //把这个id赋值到出库的值
        Takeout takeout = new Takeout();
        takeout.setId(takeoutExcption.getTakeoutId());
        takeout.setExcptionId(takeoutExcption.getId());
        takeoutService.updateById(takeout);
        return takeoutExcption;
    }

    @Override
    public TakeoutExcption getTakeoutExcptionById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutExcptions", allEntries = true)
    public void updateTakeoutExcption(TakeoutExcption takeoutExcption) {
        baseMapper.updateById(takeoutExcption);
        /**
	*预留编辑代码
	*/
        takeoutService.backTakeout(takeoutExcption.getTakeoutId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutExcptions", allEntries = true)
    public void deleteTakeoutExcption(TakeoutExcption takeoutExcption) {
        takeoutExcption.setDelFlag(true);
        baseMapper.updateById(takeoutExcption);
    }

    @Override
    @Cacheable("TakeoutExcptions")
    public List<TakeoutExcption> selectAll() {
        QueryWrapper<TakeoutExcption> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
