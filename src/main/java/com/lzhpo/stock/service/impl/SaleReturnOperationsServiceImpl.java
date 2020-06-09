package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.SaleReturnOperations;
import com.lzhpo.stock.mapper.SaleReturnOperationsMapper;
import com.lzhpo.stock.service.ISaleReturnOperationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 出库操作表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class SaleReturnOperationsServiceImpl extends ServiceImpl<SaleReturnOperationsMapper, SaleReturnOperations> implements ISaleReturnOperationsService {
	@Override
    public long getSaleReturnOperationsCount(String name) {
        QueryWrapper<SaleReturnOperations> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturnOperationss", allEntries = true)
    public SaleReturnOperations saveSaleReturnOperations(SaleReturnOperations saleReturnOperations) {
        baseMapper.insert(saleReturnOperations);
        /**
	*预留编辑代码 
	*/
        return saleReturnOperations;
    }

    @Override
    public SaleReturnOperations getSaleReturnOperationsById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturnOperationss", allEntries = true)
    public void updateSaleReturnOperations(SaleReturnOperations saleReturnOperations) {
        baseMapper.updateById(saleReturnOperations);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "SaleReturnOperationss", allEntries = true)
    public void deleteSaleReturnOperations(SaleReturnOperations saleReturnOperations) {
        saleReturnOperations.setDelFlag(true);
        baseMapper.updateById(saleReturnOperations);
    }

    @Override
    @Cacheable("SaleReturnOperationss")
    public List<SaleReturnOperations> selectAll() {
        QueryWrapper<SaleReturnOperations> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
