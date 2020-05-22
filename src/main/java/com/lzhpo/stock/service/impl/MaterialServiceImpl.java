package com.lzhpo.stock.service.impl;

import java.time.LocalDate;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.mapper.MaterialMapper;
import com.lzhpo.stock.service.IMaterialService;

/**
 * <p>
 * 仓储明细表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements IMaterialService {
	@Override
	public long getMaterialCount(String name) {
		QueryWrapper<Material> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Materials", allEntries = true)
	public Material saveMaterial(Material material) {
		baseMapper.insert(material);
		/**
		 * 预留编辑代码
		 */
		return material;
	}

	@Override
	public Material getMaterialById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Materials", allEntries = true)
	public void updateMaterial(Material material) {
		baseMapper.updateById(material);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Materials", allEntries = true)
	public void deleteMaterial(Material material) {
		material.setDelFlag(true);
		baseMapper.updateById(material);
	}

	@Override
	@Cacheable("Materials")
	public List<Material> selectAll() {
		QueryWrapper<Material> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public Material getMaterialByItemId(String itemid, LocalDate batch) {
		QueryWrapper<Material> wrapper = new QueryWrapper<>();
		wrapper.eq("item_id", itemid);
		wrapper.eq("batch_number", batch);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public void lockMaterial (String materialId, Integer number) throws Exception  {
		Material material = baseMapper.selectById(materialId);
		Integer num = material.getAvailableNum()-number;
		if(num<0){
			throw new RuntimeJsonMappingException("库存数量不足");
		}else{
			material.setAvailableNum(num);
			material.setLockCode(material.getLockCode()+number);
		}
		baseMapper.updateById(material);
	}

	@Override
	public void unlockMaterial(String materialId, Integer number) {
		Material material = baseMapper.selectById(materialId);
		material.setAvailableNum(material.getAvailableNum()+number);
		material.setLockCode(material.getLockCode()-number);
		baseMapper.updateById(material);
	}

}
