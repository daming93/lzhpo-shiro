package com.lzhpo.stock.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.mapper.MaterialMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
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
	@Autowired
	private IMaterialDepotService materialDepotService;
	
	
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
	public Material getMaterialByItemId(String itemid, LocalDate batch,Integer materialType) {
		QueryWrapper<Material> wrapper = new QueryWrapper<>();
		wrapper.eq("item_id", itemid);
		wrapper.eq("batch_number", batch);
		wrapper.eq("type", materialType);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public List<MaterialDepot>  lockMaterial (String materialId, Integer number,String depotCode) throws Exception  {
		Material material = baseMapper.selectById(materialId);
		Integer num = material.getAvailableNum()-number;
		List<MaterialDepot> list = new ArrayList<MaterialDepot>();
		if(num<0){
			throw new RuntimeJsonMappingException("库存数量不足");
		}else{
			material.setAvailableNum(num);
			material.setLockCode(material.getLockCode()+number);
			//返回一个List 其中放着 对应的储位code 和 对应 数量
			//分配从储位中拿走的数量
			try {
				list = materialDepotService.getListByMaterialAndNumber(materialId, number,depotCode);
			} catch (RuntimeJsonMappingException e) {
				throw new RuntimeJsonMappingException("库存数量不足");
			}catch (Exception e) {
				e.printStackTrace();
			}
			baseMapper.updateById(material);
		}
		return list;
	}

	@Override
	public void unlockMaterial(String materialId, Integer number) {
		Material material = baseMapper.selectById(materialId);
		material.setAvailableNum(material.getAvailableNum()+number);
		material.setLockCode(material.getLockCode()-number);
		baseMapper.updateById(material);
	}

	@Override
	public Map<String, Object> selectMaterial(String itemName, String startTime, String endTime, Integer start,
			Integer limit, Integer type,String continuity) {
		List<Material> list =baseMapper.selectMaterial(itemName, startTime, endTime, start, limit, type,continuity);
		Long count = baseMapper.selectMaterialCount(itemName, startTime, endTime, start, limit, type,continuity);
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		return map;
	}

	@Override
	public Map<String, Object> selectMaterialByDepot(Integer start, Integer limit, String depotCode) {
		List<Material> list =baseMapper.selectMaterialByDepot(start, limit, depotCode,null,null,null);
		Long count = baseMapper.selectMaterialByDepotCount(depotCode,null,null);
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		return map;
	}

	@Override
	public Map<String, Object> selectMaterialByDepot(Integer start, Integer limit, String depotCode,
			String itemId,String batch,String materialDepotId) {
		List<Material> list =baseMapper.selectMaterialByDepot(start, limit, depotCode,itemId,batch,materialDepotId);
		Long count = baseMapper.selectMaterialByDepotCount(depotCode,itemId,batch);
		Map<String,Object> map = new HashMap<>();
		map.put("list", list);
		map.put("count", count);
		return map;
	}

}
