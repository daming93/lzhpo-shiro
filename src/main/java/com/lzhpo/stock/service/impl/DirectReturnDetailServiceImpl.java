package com.lzhpo.stock.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.stock.entity.DirectReturnDetail;
import com.lzhpo.stock.mapper.DirectReturnDetailMapper;
import com.lzhpo.stock.service.IDirectReturnDetailService;

/**
 * <p>
 * 退货详情表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class DirectReturnDetailServiceImpl extends ServiceImpl<DirectReturnDetailMapper, DirectReturnDetail>
		implements IDirectReturnDetailService {
	@Override
	public long getDirectReturnDetailCount(String name) {
		QueryWrapper<DirectReturnDetail> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnDetails", allEntries = true)
	public DirectReturnDetail saveDirectReturnDetail(DirectReturnDetail directReturnDetail) {
		baseMapper.insert(directReturnDetail);
		/**
		 * 预留编辑代码
		 */
		return directReturnDetail;
	}

	@Override
	public DirectReturnDetail getDirectReturnDetailById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnDetails", allEntries = true)
	public void updateDirectReturnDetail(DirectReturnDetail directReturnDetail) {
		baseMapper.updateById(directReturnDetail);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturnDetails", allEntries = true)
	public void deleteDirectReturnDetail(DirectReturnDetail directReturnDetail) {
		directReturnDetail.setDelFlag(true);
		baseMapper.updateById(directReturnDetail);
	}

	@Override
	@Cacheable("DirectReturnDetails")
	public List<DirectReturnDetail> selectAll() {
		QueryWrapper<DirectReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void deleteDirectReturnDetailByReturnId(String returnId) {
		QueryWrapper<DirectReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("direct_return_id", returnId);
		baseMapper.delete(wrapper);
	}

	@Override
	public List<DirectReturnDetail> selectDirectReturnDetailByDirectReturnId(String returnId) {
		QueryWrapper<DirectReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("direct_return_id", returnId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public Set<String> selectDirectReturnIdsByMaterialId(String materialId) {
		QueryWrapper<DirectReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag",false);
	    wrapper.eq("material_id",materialId);
	    List<DirectReturnDetail> list = baseMapper.selectList(wrapper);
	    Set<String> str = new HashSet<>();
	    for (DirectReturnDetail detail : list) {
	    	str.add(detail.getDirectReturnId());
		}
		return str;
	}

}
