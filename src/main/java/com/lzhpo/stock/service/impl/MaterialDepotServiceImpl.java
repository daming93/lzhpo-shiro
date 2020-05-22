package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.mapper.MaterialDepotMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 物料和储位对应表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
@Service
public class MaterialDepotServiceImpl extends ServiceImpl<MaterialDepotMapper, MaterialDepot> implements IMaterialDepotService {
	@Override
    public long getMaterialDepotCount(String name) {
        QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialDepots", allEntries = true)
    public MaterialDepot saveMaterialDepot(MaterialDepot materialDepot) {
        baseMapper.insert(materialDepot);
        /**
	*预留编辑代码 
	*/
        return materialDepot;
    }

    @Override
    public MaterialDepot getMaterialDepotById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialDepots", allEntries = true)
    public void updateMaterialDepot(MaterialDepot materialDepot) {
        baseMapper.updateById(materialDepot);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialDepots", allEntries = true)
    public void deleteMaterialDepot(MaterialDepot materialDepot) {
        materialDepot.setDelFlag(true);
        baseMapper.updateById(materialDepot);
    }

    @Override
    @Cacheable("MaterialDepots")
    public List<MaterialDepot> selectAll() {
        QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void mathNumberBymaterialIdAndDepotId(String materialId, String depotId, Integer number, boolean math) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
        wrapper.eq("material_id",materialId);
        wrapper.eq("depot_id",depotId);
        MaterialDepot depotRef = baseMapper.selectOne(wrapper);
		if(depotRef!=null){
			if(math){
				depotRef.setNumber(depotRef.getNumber()+number);
			}else{
				//减要讲究一点
				depotRef.setNumber(depotRef.getNumber()-number);
			}
		}else{
			depotRef = new MaterialDepot();
			depotRef.setMaterialId(materialId);
			depotRef.setDepotId(depotId);
			depotRef.setNumber(number);
			baseMapper.insert(depotRef);
		}
	}

    
}
