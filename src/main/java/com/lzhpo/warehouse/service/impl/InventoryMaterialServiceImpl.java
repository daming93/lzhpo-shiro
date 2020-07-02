package com.lzhpo.warehouse.service.impl;

import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.warehouse.entity.InventoryMaterial;
import com.lzhpo.warehouse.mapper.InventoryMaterialMapper;
import com.lzhpo.warehouse.service.IInventoryMaterialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 盘点物料表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
@Service
public class InventoryMaterialServiceImpl extends ServiceImpl<InventoryMaterialMapper, InventoryMaterial> implements IInventoryMaterialService {
	@Override
    public long getInventoryMaterialCount(String name) {
        QueryWrapper<InventoryMaterial> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "InventoryMaterials", allEntries = true)
    public InventoryMaterial saveInventoryMaterial(InventoryMaterial inventoryMaterial) {
        baseMapper.insert(inventoryMaterial);
        /**
	*预留编辑代码 
	*/
        return inventoryMaterial;
    }

    @Override
    public InventoryMaterial getInventoryMaterialById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "InventoryMaterials", allEntries = true)
    public void updateInventoryMaterial(InventoryMaterial inventoryMaterial) {
        baseMapper.updateById(inventoryMaterial);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "InventoryMaterials", allEntries = true)
    public void deleteInventoryMaterial(InventoryMaterial inventoryMaterial) {
        inventoryMaterial.setDelFlag(true);
        baseMapper.updateById(inventoryMaterial);
    }

    @Override
    @Cacheable("InventoryMaterials")
    public List<InventoryMaterial> selectAll() {
        QueryWrapper<InventoryMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public long selectDetailDiffenent(String inventoryId) {
		QueryWrapper<InventoryMaterial> wrapper = new QueryWrapper<>();
	    wrapper.eq("del_flag",false);
	    wrapper.isNotNull("difference");
	    wrapper.ne("difference", 0);
	    wrapper.eq("inventory_id", inventoryId);
	    changgeDetailInComplete(inventoryId);
		return baseMapper.selectCount(wrapper);
	}
	private void changgeDetailInComplete(String inventoryId){
		QueryWrapper<InventoryMaterial> wrapper = new QueryWrapper<>();
	    wrapper.eq("del_flag",false);
	    wrapper.eq("inventory_id", inventoryId);
	    List<InventoryMaterial> lists = baseMapper.selectList(wrapper);
		// 正常 check_type_normal
		Integer check_type_normal = CacheUtils.keyDict.get("check_type_normal").getValue();

		// 差异 check_type_error
		Integer check_type_error = CacheUtils.keyDict.get("check_type_error").getValue();
	    for (InventoryMaterial inventoryMaterial : lists) {
			if(inventoryMaterial.getInventoryNum()==null){
				inventoryMaterial.setInventoryNum(inventoryMaterial.getDepotNum());
			}
			if(inventoryMaterial.getDifference()==null){
				inventoryMaterial.setDifference(0l);
			}
			if(inventoryMaterial.getDifference().equals(0l)){//没有差异的
				inventoryMaterial.setCheckType(check_type_normal);
			}else{
				inventoryMaterial.setCheckType(check_type_error);
			}
			baseMapper.updateById(inventoryMaterial);
		}
	}

}
