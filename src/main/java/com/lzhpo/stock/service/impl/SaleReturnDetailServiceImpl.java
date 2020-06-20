package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.SaleReturnDetail;
import com.lzhpo.stock.entity.StorageDetail;
import com.lzhpo.stock.mapper.SaleReturnDetailMapper;
import com.lzhpo.stock.service.ISaleReturnDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 退货详情表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class SaleReturnDetailServiceImpl extends ServiceImpl<SaleReturnDetailMapper, SaleReturnDetail>
		implements ISaleReturnDetailService {
	@Override
	public long getSaleReturnDetailCount(String name) {
		QueryWrapper<SaleReturnDetail> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturnDetails", allEntries = true)
	public SaleReturnDetail saveSaleReturnDetail(SaleReturnDetail saleReturnDetail) {
		baseMapper.insert(saleReturnDetail);
		/**
		 * 预留编辑代码
		 */
		return saleReturnDetail;
	}

	@Override
	public SaleReturnDetail getSaleReturnDetailById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturnDetails", allEntries = true)
	public void updateSaleReturnDetail(SaleReturnDetail saleReturnDetail) {
		baseMapper.updateById(saleReturnDetail);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturnDetails", allEntries = true)
	public void deleteSaleReturnDetail(SaleReturnDetail saleReturnDetail) {
		saleReturnDetail.setDelFlag(true);
		baseMapper.updateById(saleReturnDetail);
	}

	@Override
	@Cacheable("SaleReturnDetails")
	public List<SaleReturnDetail> selectAll() {
		QueryWrapper<SaleReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void deleteSaleReturnDetailByReturnId(String returnId) {
		QueryWrapper<SaleReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("sales_return_id", returnId);
		baseMapper.delete(wrapper);
	}

	@Override
	public List<SaleReturnDetail> selectSaleReturnDetailBySaleReturnId(String returnId) {
		QueryWrapper<SaleReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("sales_return_id", returnId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public Set<String> selectSaleReturnIdsByMaterialId(String materialId) {
		QueryWrapper<SaleReturnDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag",false);
	    wrapper.eq("material_id",materialId);
	    List<SaleReturnDetail> list = baseMapper.selectList(wrapper);
	    Set<String> str = new HashSet<>();
	    for (SaleReturnDetail detail : list) {
	    	str.add(detail.getSalesReturnId());
		}
		return str;
	}

}
