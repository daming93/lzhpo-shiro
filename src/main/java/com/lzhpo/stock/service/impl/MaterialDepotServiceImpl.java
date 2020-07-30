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
public class MaterialDepotServiceImpl extends ServiceImpl<MaterialDepotMapper, MaterialDepot>
		implements IMaterialDepotService {
	@Override
	public long getMaterialDepotCount(String name) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialDepots", allEntries = true)
	public MaterialDepot saveMaterialDepot(MaterialDepot materialDepot) {
		baseMapper.insert(materialDepot);
		/**
		 * 预留编辑代码
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
		 * 预留编辑代码
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
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void mathNumberBymaterialIdAndDepotId(String materialId, String depotId, Integer number, Integer wholeNum,
			Integer satterNum, boolean math) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
		wrapper.eq("material_id", materialId);
		wrapper.eq("depot_id", depotId);
		MaterialDepot depotRef = baseMapper.selectOne(wrapper);
		if (depotRef != null) {
			if (math) {
				depotRef.setNumber(depotRef.getNumber() + number);
				depotRef.setWholeNum(depotRef.getWholeNum() + wholeNum);// 增加整库存
				depotRef.setScatteredNum(depotRef.getScatteredNum() + satterNum);// 增加零库存
			} else {
				// 减要讲究一点 //负数量
				// 这里对应着入库的时候的数量
				depotRef.setNumber(depotRef.getNumber() - number);
				depotRef.setWholeNum(depotRef.getWholeNum() - wholeNum);// 减少整库存
				depotRef.setScatteredNum(depotRef.getScatteredNum() - satterNum);// 减少零库存
			}
			baseMapper.updateById(depotRef);
		} else {
			depotRef = new MaterialDepot();
			depotRef.setMaterialId(materialId);
			depotRef.setDepotId(depotId);
			depotRef.setNumber(number);
			depotRef.setWholeNum(wholeNum);// 增加整库存
			depotRef.setScatteredNum(satterNum);// 增加零库存
			depotRef.setId(UUID.randomUUID().toString());
			baseMapper.insert(depotRef);
		}
	}

	@Override
	public List<MaterialDepot> getListByMaterialAndNumber(String materialId, Integer number, Integer wholeNum,Integer scatteredNum,String depotCode,Integer rate)
			throws Exception {
		List<MaterialDepot> resultList = new ArrayList<MaterialDepot>();
		if (depotCode == null) {
			// 有物料 有数量
			// 找到对应 物料
			QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
			wrapper.eq("material_id", materialId);
			wrapper.ne("number", 0);
			wrapper.orderByAsc("depot_id");
			List<MaterialDepot> list = baseMapper.selectList(wrapper);
			// 搞个总数
			Integer surplusSum = 0;
			Integer wholeNumSum = 0;
			Integer scatteredNumSum = 0;
			Integer scatteredNumSurplus = 0; //零数余量
			Integer needScattered = 0; //需要拆零得数
			Integer scatteredSurplus = 0;
			for (MaterialDepot materialDepot : list) {
				scatteredNumSum += materialDepot.getScatteredNum();
			}
			if(scatteredNumSum<scatteredNum){//库存数量不够发 便需要拆
				scatteredNumSurplus = scatteredNum-scatteredNumSum;
				//余下得转整/零
				wholeNum+=scatteredNumSurplus/rate;
				needScattered = scatteredNumSurplus%rate;
			}
			for (MaterialDepot materialDepot : list) {
				// 这里可以处理 分配 的问题
				// 出库的时候使用 在新建出库之后 自动分配储位
				surplusSum = materialDepot.getNumber() - number; //这个数 不能做标准了 要整零分开
				wholeNumSum = materialDepot.getWholeNum() -wholeNum;
				scatteredSurplus = materialDepot.getScatteredNum() - scatteredNum;
				
			//	wholeNumSurplus = wholeNumSurplus - materialDepot.getWholeNum();
				MaterialDepot mDepot = new MaterialDepot();
				mDepot.setDepotId(materialDepot.getDepotId());
				//先不考虑之前逻辑
				//整零 就是 补齐所有整零
				//拆零是怎么考虑  前端传过来得 先发完其他得 最后一件才拆零 
				//先 看零数量够不够
				//如果需要拆零就看数量是不是 比需要用得整数量大一 然后在使用转换率
				if(wholeNumSum>0&&needScattered!=0){//就是这个储位得库存得整数余量大于1 有拆零得 条件 看需不要要拆零
					//如果需要拆零 并且有拆零条件 整数够用了
					mDepot.setWholeNum(wholeNum);
					mDepot.setScatteredNum(scatteredNum); // 用了这么多 这个数字要拿出去展示
					materialDepot.setWholeNum(wholeNumSum-1);
					materialDepot.setScatteredNum(materialDepot.getScatteredNum()+rate- scatteredNum);
					wholeNum = 0;//制空
					scatteredSurplus = 0;//需要拆零 第一个就拆了 
				}else if(wholeNumSum>=0){
					mDepot.setWholeNum(wholeNum);
					materialDepot.setWholeNum(wholeNumSum);
					wholeNum = 0;//制空
				}else{//这就是不够得时候
					mDepot.setWholeNum(materialDepot.getWholeNum());
					wholeNum = wholeNum - materialDepot.getWholeNum(); // 不够用
					materialDepot.setWholeNum(0);
				}
				
				//以下为零数量
				if (scatteredSurplus >= 0) {// 够用了
					mDepot.setScatteredNum(scatteredNum); // 用了这么多 这个数字要拿出去展示
					materialDepot.setScatteredNum(scatteredSurplus); // 这个数字用来更新
					scatteredNum = 0;// 够用把这个数字置为 0 最后这个如果还不够 就抛出 库存不足的异常
				} else { // 不够用
					mDepot.setScatteredNum(materialDepot.getScatteredNum());
					scatteredNum = scatteredNum - materialDepot.getScatteredNum(); // 不够用
																	// 把缺的部分进入下次循环
					materialDepot.setScatteredNum(0); // 就是这个所有都用了
					
				}
				//算一下他的数量
				mDepot.setNumber(mDepot.getWholeNum()*rate+mDepot.getScatteredNum()); // 用了这么多 这个数字要拿出去展示
				materialDepot.setNumber(materialDepot.getWholeNum()*rate+materialDepot.getScatteredNum());
				
//				if (surplusSum >= 0) {// 够用了
//					mDepot.setNumber(number); // 用了这么多 这个数字要拿出去展示
//					materialDepot.setNumber(surplusSum); // 这个数字用来更新
//					baseMapper.updateById(materialDepot);
//					number = 0;// 够用把这个数字置为 0 最后这个如果还不够 就抛出 库存不足的异常
//				} else { // 不够用
//					mDepot.setNumber(materialDepot.getNumber());
//					number = number - materialDepot.getNumber(); // 不够用
//																	// 把缺的部分进入下次循环
//					materialDepot.setNumber(0); // 就是这个所有都用了
//					baseMapper.updateById(materialDepot);
//				}
				if(mDepot.getNumber()!=0){
					baseMapper.updateById(materialDepot);
					resultList.add(mDepot); // 够用数字要边下
				}
			}
		} else {// 提供储位号的话
			QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
			wrapper.eq("depot_id", depotCode);
			wrapper.eq("material_id", materialId);
			MaterialDepot mDepot = baseMapper.selectOne(wrapper);
			// 应该只有一个
			if (mDepot != null) { // 把数量加上或者减去 看变化值
				if (mDepot.getNumber() - number < 0) {
					throw new RuntimeJsonMappingException("库存不足");
				} else {
					mDepot.setNumber(mDepot.getNumber() - number);
					baseMapper.updateById(mDepot);
				}
			}
		}
		return resultList;
	}

	@Override
	public MaterialDepot getMaterialDepotByMaterialIdAndDepotCode(String materialId, String depotCode) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
		wrapper.eq("depot_id", depotCode);
		wrapper.eq("material_id", materialId);
		return baseMapper.selectOne(wrapper);
	}

	@Override
	public boolean hasNumberInDepot(String depotCode) {
		QueryWrapper<MaterialDepot> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("depot_id", depotCode);
		wrapper.gt("number",0);
		// wrapper.eq("name",name);
		long count = baseMapper.selectCount(wrapper);
		return  count>0?true:false ;
	}

}
