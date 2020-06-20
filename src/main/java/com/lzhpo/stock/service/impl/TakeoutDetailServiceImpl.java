package com.lzhpo.stock.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.entity.TakeoutDetail;
import com.lzhpo.stock.mapper.TakeoutDetailMapper;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.ITakeoutDetailService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
@Service
public class TakeoutDetailServiceImpl extends ServiceImpl<TakeoutDetailMapper, TakeoutDetail>
		implements ITakeoutDetailService {
	@Autowired
	private IMaterialService materialSerivice;

	@Autowired
	private IClientitemService clientitemService;


	@Autowired
	private IMaterialOperationsService materialOperationsService;

	@Override
	public long getTakeoutDetailCount(String name) {
		QueryWrapper<TakeoutDetail> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "TakeoutDetails", allEntries = true)
	public TakeoutDetail saveTakeoutDetail(TakeoutDetail takeoutDetail) {
		baseMapper.insert(takeoutDetail);
		/**
		 * 预留编辑代码
		 */
		return takeoutDetail;
	}

	@Override
	public TakeoutDetail getTakeoutDetailById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "TakeoutDetails", allEntries = true)
	public void updateTakeoutDetail(TakeoutDetail takeoutDetail) {
		baseMapper.updateById(takeoutDetail);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "TakeoutDetails", allEntries = true)
	public void deleteTakeoutDetail(TakeoutDetail takeoutDetail,String code) {
		//撤销出库
		Integer trunover_type_takeout_back = CacheUtils.keyDict.get("trunover_type_takeout_back").getValue();
		// 删除需要返回库存
		// 防止前台传来得数量是修改过得
		TakeoutDetail entity = baseMapper.selectById(takeoutDetail.getId());
		// 解锁
		materialSerivice.unlockMaterial(entity.getMaterial(), entity.getNumber());
		// 记录流水
		MaterialOperations materialOperations = new MaterialOperations();
		materialOperations.setFromCode(code);
		materialOperations.setFromType(trunover_type_takeout_back);//撤销出库
		materialOperations.setMaterialId(takeoutDetail.getMaterial());
		materialOperations.setNumber(0 - entity.getNumber());
		materialOperations.setType(2);// 撤销出库为+
		materialOperationsService.save(materialOperations);
		takeoutDetail.setDelFlag(true);
		baseMapper.deleteById(takeoutDetail.getId());//就直接删除
	}

	@Override
	@Cacheable("TakeoutDetails")
	public List<TakeoutDetail> selectAll() {
		QueryWrapper<TakeoutDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void updateNumber(TakeoutDetail takeoutDetail,String code) throws Exception {
		//编辑出库
		Integer trunover_type_takeout_edit = CacheUtils.keyDict.get("trunover_type_takeout_edit").getValue();
		// 防止前台传来得数量是修改过得
		TakeoutDetail entity = baseMapper.selectById(takeoutDetail.getId());
	
		
		Clientitem item = clientitemService.getById(entity.getItemId());
		
		if (takeoutDetail.getNumZ() != null) {
			takeoutDetail.setNumber(CommomUtil.AllToOne(takeoutDetail.getNumZ(), item.getUnitRate()));
		}
		// 然后在锁住修改过的数量 传的数字就是变更的数字
		materialSerivice.lockMaterial(takeoutDetail.getMaterial(), takeoutDetail.getNumber() - entity.getNumber(),entity.getDepot());
		// 记录流水
		MaterialOperations materialOperations = new MaterialOperations();
		materialOperations.setFromCode(code);
		materialOperations.setFromType(trunover_type_takeout_edit);// 出库
		materialOperations.setMaterialId(takeoutDetail.getMaterial());
		materialOperations.setNumber(takeoutDetail.getNumber() - entity.getNumber());
		materialOperations.setType(2);// 出库为-
		materialOperationsService.save(materialOperations);

		baseMapper.updateById(takeoutDetail);
	}

	@Override
	public List<TakeoutDetail> selecttakeoutDetailBytakeoutId(String id) {
		QueryWrapper<TakeoutDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("takeout_id", id);
		return baseMapper.selectList(wrapper);
	}

}
