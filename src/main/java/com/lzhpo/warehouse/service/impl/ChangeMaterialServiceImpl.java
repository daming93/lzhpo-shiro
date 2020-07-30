package com.lzhpo.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.warehouse.entity.ChangeMaterial;
import com.lzhpo.warehouse.mapper.ChangeMaterialMapper;
import com.lzhpo.warehouse.service.IChangeMaterialService;
/**
 * <p>
 * 调仓物料表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-03
 */
@Service
public class ChangeMaterialServiceImpl extends ServiceImpl<ChangeMaterialMapper, ChangeMaterial> implements IChangeMaterialService {
	@Autowired
	private IMaterialDepotService  materialDepotService;
	
	@Override
    public long getChangeMaterialCount(String name) {
        QueryWrapper<ChangeMaterial> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ChangeMaterials", allEntries = true)
    public ChangeMaterial saveChangeMaterial(ChangeMaterial changeMaterial) {
    	/*
    	 * 找到老的数据 减去调整的数量
    	 */
    	materialDepotService.mathNumberBymaterialIdAndDepotId(changeMaterial.getMaterialId(), changeMaterial.getFdepot(), changeMaterial.getNownum(),changeMaterial.getNewWholeNum(),changeMaterial.getNewScatteredNum(), false);
    	//新的分配储位
    	materialDepotService.mathNumberBymaterialIdAndDepotId(changeMaterial.getMaterialId(), changeMaterial.getNdepot(), changeMaterial.getNownum(), changeMaterial.getNewWholeNum(),changeMaterial.getNewScatteredNum(),true);
        baseMapper.insert(changeMaterial);
        /**
	*预留编辑代码 
	*/
        return changeMaterial;
    }

    @Override
    public ChangeMaterial getChangeMaterialById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ChangeMaterials", allEntries = true)
    public void updateChangeMaterial(ChangeMaterial changeMaterial) {
        baseMapper.updateById(changeMaterial);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ChangeMaterials", allEntries = true)
    public void deleteChangeMaterial(ChangeMaterial changeMaterial) {
        changeMaterial.setDelFlag(true);
        baseMapper.updateById(changeMaterial);
    }

    @Override
    @Cacheable("ChangeMaterials")
    public List<ChangeMaterial> selectAll() {
        QueryWrapper<ChangeMaterial> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
