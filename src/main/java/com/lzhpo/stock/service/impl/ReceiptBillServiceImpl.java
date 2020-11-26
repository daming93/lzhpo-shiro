package com.lzhpo.stock.service.impl;

import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.stock.entity.ReceiptBill;
import com.lzhpo.stock.mapper.ReceiptBillMapper;
import com.lzhpo.stock.service.IReceiptBillService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-11-23
 */
@Service
public class ReceiptBillServiceImpl extends ServiceImpl<ReceiptBillMapper, ReceiptBill> implements IReceiptBillService {
	@Override
	public long getReceiptBillCount(String name) {
		QueryWrapper<ReceiptBill> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "ReceiptBills", allEntries = true)
	public ReceiptBill saveReceiptBill(ReceiptBill receiptBill) {
		baseMapper.insert(receiptBill);
		/**
		 * 预留编辑代码
		 */
		return receiptBill;
	}

	@Override
	public ReceiptBill getReceiptBillById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "ReceiptBills", allEntries = true)
	public void updateReceiptBill(ReceiptBill receiptBill) {
		// 已确认 3
		Integer receipt_status_yes = CacheUtils.keyDict.get("receipt_status_yes").getValue();	
		// 待审核 2
		Integer receipt_status_audit = CacheUtils.keyDict.get("receipt_status_audit").getValue();	
		if(receiptBill.getType()!=null){
			if(receiptBill.getType().equals(1)){//首审
				receiptBill.setDeliveryReceiptTime(LocalDateTime.now());
				receiptBill.setReceiptStatus(receipt_status_audit);
			}
			if(receiptBill.getType().equals(2)){//终审
				receiptBill.setReceiptTime(LocalDateTime.now());
				receiptBill.setReceiptStatus(receipt_status_yes);
			}
		}
		baseMapper.updateById(receiptBill);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "ReceiptBills", allEntries = true)
	public void deleteReceiptBill(ReceiptBill receiptBill) {
		receiptBill.setDelFlag(true);
		baseMapper.updateById(receiptBill);
	}

	@Override
	@Cacheable("ReceiptBills")
	public List<ReceiptBill> selectAll() {
		QueryWrapper<ReceiptBill> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void deleteReceiptBillByTakeoutId(String takeoutId) {
		QueryWrapper<ReceiptBill> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("ref_id", takeoutId);
		baseMapper.delete(wrapper);
	}

	@Override
	public ReceiptBill getReceiptBillByTakeoutId(String takeoutId) {
		QueryWrapper<ReceiptBill> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("ref_id", takeoutId);
		return baseMapper.selectList(wrapper) != null && baseMapper.selectList(wrapper).size() > 0
				? baseMapper.selectList(wrapper).get(0) : null;
	}

	@Override
	public void changeStatusByTakeoutId(String takeoutId,Integer receiptStatus) {
		QueryWrapper<ReceiptBill> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("ref_id", takeoutId);
		ReceiptBill entity = new ReceiptBill();
		entity.setReceiptStatus(receiptStatus);
		baseMapper.update(entity, wrapper);
		
	}

	@Override
	public void changeStatusById(String id, Integer receiptStatus) {
		ReceiptBill entity = getById(id);
		entity.setReceiptStatus(receiptStatus);
		baseMapper.updateById(entity);
	}
}
