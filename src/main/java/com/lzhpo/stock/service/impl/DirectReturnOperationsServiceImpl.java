package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.DirectReturnOperations;
import com.lzhpo.stock.mapper.DirectReturnOperationsMapper;
import com.lzhpo.stock.service.IDirectReturnOperationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 出库操作表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class DirectReturnOperationsServiceImpl extends ServiceImpl<DirectReturnOperationsMapper, DirectReturnOperations>
		implements IDirectReturnOperationsService {
	@Override
	public long getDirectReturnOperationsCount(String name) {
		QueryWrapper<DirectReturnOperations> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnOperationss", allEntries = true)
	public DirectReturnOperations saveDirectReturnOperations(DirectReturnOperations DirectReturnOperations) {
		baseMapper.insert(DirectReturnOperations);
		/**
		 * 预留编辑代码
		 */
		return DirectReturnOperations;
	}

	@Override
	public DirectReturnOperations getDirectReturnOperationsById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnOperationss", allEntries = true)
	public void updateDirectReturnOperations(DirectReturnOperations DirectReturnOperations) {
		baseMapper.updateById(DirectReturnOperations);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnOperationss", allEntries = true)
	public void deleteDirectReturnOperations(DirectReturnOperations DirectReturnOperations) {
		DirectReturnOperations.setDelFlag(true);
		baseMapper.updateById(DirectReturnOperations);
	}

	@Override
	@Cacheable("DirectReturnOperationss")
	public List<DirectReturnOperations> selectAll() {
		QueryWrapper<DirectReturnOperations> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<DirectReturnOperations> selectByReturnId(String returnId) {
		QueryWrapper<DirectReturnOperations> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("return_id", returnId);
		return baseMapper.selectList(wrapper);
	}

}
