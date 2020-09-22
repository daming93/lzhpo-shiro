package com.lzhpo.deliver.service.impl;

import com.lzhpo.deliver.entity.DispatchReceiptBill;
import com.lzhpo.deliver.mapper.DispatchReceiptBillMapper;
import com.lzhpo.deliver.service.IDispatchReceiptBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
@Service
public class DispatchReceiptBillServiceImpl extends ServiceImpl<DispatchReceiptBillMapper, DispatchReceiptBill> implements IDispatchReceiptBillService {
	@Override
    public long getDispatchReceiptBillCount(String name) {
        QueryWrapper<DispatchReceiptBill> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchReceiptBills", allEntries = true)
    public DispatchReceiptBill saveDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill) {
        baseMapper.insert(dispatchReceiptBill);
        /**
	*预留编辑代码 
	*/
        return dispatchReceiptBill;
    }

    @Override
    public DispatchReceiptBill getDispatchReceiptBillById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchReceiptBills", allEntries = true)
    public void updateDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill) {
        baseMapper.updateById(dispatchReceiptBill);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DispatchReceiptBills", allEntries = true)
    public void deleteDispatchReceiptBill(DispatchReceiptBill dispatchReceiptBill) {
        dispatchReceiptBill.setDelFlag(true);
        baseMapper.updateById(dispatchReceiptBill);
    }

    @Override
    @Cacheable("DispatchReceiptBills")
    public List<DispatchReceiptBill> selectAll() {
        QueryWrapper<DispatchReceiptBill> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
