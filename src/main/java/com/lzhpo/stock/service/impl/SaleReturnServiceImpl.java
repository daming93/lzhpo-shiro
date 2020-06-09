package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.SaleReturn;
import com.lzhpo.stock.mapper.SaleReturnMapper;
import com.lzhpo.stock.service.ISaleReturnService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 退货表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class SaleReturnServiceImpl extends ServiceImpl<SaleReturnMapper, SaleReturn> implements ISaleReturnService {
	@Override
    public long getSaleReturnCount(String name) {
        QueryWrapper<SaleReturn> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturns", allEntries = true)
    public SaleReturn saveSaleReturn(SaleReturn saleReturn) {
        baseMapper.insert(saleReturn);
        /**
	*预留编辑代码 
	*/
        return saleReturn;
    }

    @Override
    public SaleReturn getSaleReturnById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturns", allEntries = true)
    public void updateSaleReturn(SaleReturn saleReturn) {
        baseMapper.updateById(saleReturn);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturns", allEntries = true)
    public void deleteSaleReturn(SaleReturn saleReturn) {
        saleReturn.setDelFlag(true);
        baseMapper.updateById(saleReturn);
    }

    @Override
    @Cacheable("SaleReturns")
    public List<SaleReturn> selectAll() {
        QueryWrapper<SaleReturn> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
