package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.mapper.MaterialDepotMapper;
import com.lzhpo.stock.service.IMaterialDepotService;

import cn.hutool.core.lang.UUID;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
				//减要讲究一点 //负数量  
				//这里对应着入库的时候的数量
				depotRef.setNumber(depotRef.getNumber()-number);
			}
			baseMapper.updateById(depotRef);
		}else{
			depotRef = new MaterialDepot();
			depotRef.setMaterialId(materialId);
			depotRef.setDepotId(depotId);
			depotRef.setNumber(number);
			depotRef.setId(UUID.randomUUID().toString());
			baseMapper.insert(depotRef);
		}
	}

	@Override
	public List<MaterialDepot> getListByMaterialAndNumber(String materialId, Integer number,String depotCode) throws Exception {
	    List<MaterialDepot> resultList = new ArrayList<MaterialDepot>();
		if(depotCode==null){
			//有物料 有数量 
			//找到对应 物料
			QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
	        wrapper.eq("material_id",materialId);
	        wrapper.ne("number",0);
	    
	        List<MaterialDepot> list = baseMapper.selectList(wrapper);
	        //搞个总数
	        Integer surplusSum = 0;
	        for (MaterialDepot materialDepot : list) {
	        	//这里可以处理 分配 的问题
	        	//出库的时候使用 在新建出库之后 自动分配储位 
	        	surplusSum = materialDepot.getNumber() - number;
	        	MaterialDepot mDepot = new MaterialDepot();
	        	mDepot.setDepotId(materialDepot.getDepotId());
	        	if(surplusSum>=0){//够用了
	        		mDepot.setNumber(number); //用了这么多 这个数字要拿出去展示
	        		materialDepot.setNumber(surplusSum); //这个数字用来更新 
	        		baseMapper.updateById(materialDepot);
	        		number = 0;//够用把这个数字置为 0 最后这个如果还不够 就抛出 库存不足的异常
	        	}else{ //不够用
	        		mDepot.setNumber(materialDepot.getNumber());
	        		number = number - materialDepot.getNumber(); //不够用 把缺的部分进入下次循环 
	        		materialDepot.setNumber(0); //就是这个所有都用了
	        		baseMapper.updateById(materialDepot);
	        	}
	        	resultList.add(mDepot); //够用数字要边下
	        	if(surplusSum>=0){
	        		return resultList;
	        	}
			}
	        if(number!=0){
	        	throw new RuntimeJsonMappingException("库存不足");
	        }
		}else{//提供储位号的话
			QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
	        wrapper.eq("depot_id",depotCode);
	        wrapper.eq("material_id",materialId);
	        MaterialDepot mDepot = baseMapper.selectOne(wrapper);
	        //应该只有一个
	       if(mDepot!=null){ //把数量加上或者减去 看变化值
	    	   if(mDepot.getNumber()-number<0){
	    		   throw new RuntimeJsonMappingException("库存不足");
	    	   }else{
	    		   mDepot.setNumber(mDepot.getNumber()-number);
	    		   baseMapper.updateById(mDepot);
	    	   }
	       }
		}
		return resultList;
	}

	@Override
	public MaterialDepot getMaterialDepotByMaterialIdAndDepotCode(String materialId, String depotCode) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
        wrapper.eq("depot_id",depotCode);
        wrapper.eq("material_id",materialId);
		return  baseMapper.selectOne(wrapper);
	}

    
}
