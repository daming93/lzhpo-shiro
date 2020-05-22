package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.mapper.MaterialOperationsMapper;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 仓储明细表-操作表可以相当于流水表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Service
public class MaterialOperationsServiceImpl extends ServiceImpl<MaterialOperationsMapper, MaterialOperations>
		implements IMaterialOperationsService {
	@Override
	public long getMaterialOperationsCount(String name) {
		QueryWrapper<MaterialOperations> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialOperationss", allEntries = true)
	public MaterialOperations saveMaterialOperations(MaterialOperations materialOperations) {
		baseMapper.insert(materialOperations);
		/**
		 * 预留编辑代码
		 */
		return materialOperations;
	}

	@Override
	public MaterialOperations getMaterialOperationsById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialOperationss", allEntries = true)
	public void updateMaterialOperations(MaterialOperations materialOperations) {
		baseMapper.updateById(materialOperations);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialOperationss", allEntries = true)
	public void deleteMaterialOperations(MaterialOperations materialOperations) {
		materialOperations.setDelFlag(true);
		baseMapper.updateById(materialOperations);
	}

	@Override
	@Cacheable("MaterialOperationss")
	public List<MaterialOperations> selectAll() {
		QueryWrapper<MaterialOperations> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<MaterialOperations> selectByMaterialId(String materialId) {
		QueryWrapper<MaterialOperations> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("material_id", materialId);
		wrapper.orderByDesc("create_date");
		return baseMapper.selectList(wrapper);
	}

	@Override
	public Map<String,Object> selectOperations(String itemName, String startTime, String endTime,Integer start,Integer limit, Integer flag) {
		List<MaterialOperations> list =baseMapper.selectOperations(itemName, startTime, endTime, start, limit, flag);
		Long count = baseMapper.selectOperationsCount(itemName, startTime, endTime, start, limit, flag);
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		return map;
	}

}
