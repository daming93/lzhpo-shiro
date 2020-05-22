package com.lzhpo.stock.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.stock.entity.MaterialTray;
import com.lzhpo.stock.mapper.MaterialTrayMapper;
import com.lzhpo.stock.service.IMaterialTrayService;
/**
 * <p>
 * 物料和托盘对应表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-14
 */
@Service
public class MaterialTrayServiceImpl extends ServiceImpl<MaterialTrayMapper, MaterialTray> implements IMaterialTrayService {
	@Override
    public long getMaterialTrayCount(String name) {
        QueryWrapper<MaterialTray> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialTrays", allEntries = true)
    public MaterialTray saveMaterialTray(MaterialTray materialTray) {
        baseMapper.insert(materialTray);
        /**
	*预留编辑代码 
	*/
        return materialTray;
    }

    @Override
    public MaterialTray getMaterialTrayById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialTrays", allEntries = true)
    public void updateMaterialTray(MaterialTray materialTray) {
        baseMapper.updateById(materialTray);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialTrays", allEntries = true)
    public void deleteMaterialTray(MaterialTray materialTray) {
        materialTray.setDelFlag(true);
        baseMapper.updateById(materialTray);
    }

    @Override
    @Cacheable("MaterialTrays")
    public List<MaterialTray> selectAll() {
        QueryWrapper<MaterialTray> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void mathNumberBymaterialIdAndTrayId(String materialId, String trayId, Integer number, boolean math) {
		QueryWrapper<MaterialTray> wrapper = new QueryWrapper<>();
        wrapper.eq("material_id",materialId);
        wrapper.eq("tray_id",trayId);
        MaterialTray trayRef = baseMapper.selectOne(wrapper);
		if(trayRef!=null){
			if(math){
				trayRef.setNumber(trayRef.getNumber()+number);
			}else{
				//减要讲究一点
				trayRef.setNumber(trayRef.getNumber()-number);
			}
		}else{
			trayRef = new MaterialTray();
			trayRef.setMaterialId(materialId);
			trayRef.setTrayId(trayId);
			trayRef.setNumber(number);
			baseMapper.insert(trayRef);
		}
	}


}
