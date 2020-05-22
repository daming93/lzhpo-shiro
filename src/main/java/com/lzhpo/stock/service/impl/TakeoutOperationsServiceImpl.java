package com.lzhpo.stock.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.stock.entity.TakeoutOperations;
import com.lzhpo.stock.mapper.TakeoutOperationsMapper;
import com.lzhpo.stock.service.ITakeoutOperationsService;
/**
 * <p>
 * 入库操作表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-18
 */
@Service
public class TakeoutOperationsServiceImpl extends ServiceImpl<TakeoutOperationsMapper, TakeoutOperations> implements ITakeoutOperationsService {
	@Override
    public long getTakeoutOperationsCount(String name) {
        QueryWrapper<TakeoutOperations> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutOperationss", allEntries = true)
    public TakeoutOperations saveTakeoutOperations(TakeoutOperations takeoutOperations) {
        baseMapper.insert(takeoutOperations);
        /**
	*预留编辑代码 
	*/
        return takeoutOperations;
    }

    @Override
    public TakeoutOperations getTakeoutOperationsById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutOperationss", allEntries = true)
    public void updateTakeoutOperations(TakeoutOperations takeoutOperations) {
        baseMapper.updateById(takeoutOperations);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "TakeoutOperationss", allEntries = true)
    public void deleteTakeoutOperations(TakeoutOperations takeoutOperations) {
        takeoutOperations.setDelFlag(true);
        baseMapper.updateById(takeoutOperations);
    }

    @Override
    @Cacheable("TakeoutOperationss")
    public List<TakeoutOperations> selectAll() {
        QueryWrapper<TakeoutOperations> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public List<TakeoutOperations> selectByTakeoutId(String id) {
		 QueryWrapper<TakeoutOperations> wrapper = new QueryWrapper<>();
	     wrapper.eq("takeout_id",id);
	     wrapper.orderByDesc("create_date");
	     List<TakeoutOperations> operations = baseMapper.selectList(wrapper);
	     return operations;
	}


}
