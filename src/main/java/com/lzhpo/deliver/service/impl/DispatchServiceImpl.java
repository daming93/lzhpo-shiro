package com.lzhpo.deliver.service.impl;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.deliver.entity.DispactAddress;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.ExpressBill;
import com.lzhpo.deliver.mapper.DispatchMapper;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.deliver.service.IExpressBillService;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.sys.service.IGenerateNoService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
@Service
public class DispatchServiceImpl extends ServiceImpl<DispatchMapper, Dispatch> implements IDispatchService {
	@Autowired
	private IGenerateNoService generateNoService;

	@Autowired
	private IDispactAddressService dispactAddressService;

	@Autowired
	private IExpressBillService expressBillService;

	@Autowired
	private ITakeoutService takeoutService;

	@Override
	public long getDispatchCount(String name) {
		QueryWrapper<Dispatch> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.lzhpo.deliver.service.IDispatchService#saveDispatch(com.lzhpo.deliver
	 * .entity.Dispatch)
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Dispatchs", allEntries = true)
	public Dispatch saveDispatch(Dispatch dispatch) {

		dispatch.setCode(generateNoService.nextCode("YSJH"));
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		dispatch.setStatus(modify_status_await);
		baseMapper.insert(dispatch);
		Set<DispactAddress> detaliSet = dispatch.getDetailSet();
		saveDetail(dispatch, detaliSet);
		return dispatch;
	}

	private String saveDetail(Dispatch dispatch, Set<DispactAddress> detaliSet) {
		// 已排单
		Integer scheduling_status_yes = CacheUtils.keyDict.get("scheduling_status_yes").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		
		StringBuffer bf = new StringBuffer();
		if(detaliSet!=null){
			for (DispactAddress dispactAddress : detaliSet) {
				//这里处理单据id为空的单据
				//跳过dispatchId不为空的单据
				if(StringUtils.isNotEmpty(dispactAddress.getDispacthId())){
					continue;
				}
				long count = dispactAddressService.getCountByCodeAndIDispacthId(dispactAddress.getCode(), dispactAddress.getDispacthId());
				if(count>0){//已经绑定过
					bf.append(dispactAddress.getCode() + "该单已排过  ");
					continue;
				}
				dispactAddress.setDispacthId(dispatch.getId());
				// 原表更改它得配送状态
				switch (dispactAddress.getType()) {
				case 1:// 快速发单 得表 把其配送状态改为已排单
					ExpressBill expressBill = expressBillService.getById(dispactAddress.getTableId());
					expressBill.setSchedulingStatus(scheduling_status_yes);
					// 锁定单据
					expressBill.setStatus(modify_status_lock);
					expressBillService.updateById(expressBill);
					dispactAddress.setId(null);
					dispactAddressService.save(dispactAddress);
					break;
				case 2:// 库存发单 得表 把其配送状态改为已排单
					Takeout takeout = takeoutService.getById(dispactAddress.getTableId());
					takeout.setSchedulingStatus(scheduling_status_yes);
					takeout.setStatus(modify_status_lock);
					// 锁定单据
					takeoutService.updateById(takeout);
					dispactAddress.setId(null);
					dispactAddressService.save(dispactAddress);
					break;
					
				case 3:// 快速发单得拆单
					ExpressBill expressBillSpilt = expressBillService.getById(dispactAddress.getTableId());
					expressBillSpilt.setSchedulingStatus(scheduling_status_yes);
					// 锁定单据
					expressBillSpilt.setStatus(modify_status_lock);
					expressBillService.updateById(expressBillSpilt);
					dispactAddressService.updateById(dispactAddress);
					break;
					
				case 4: // 出库单 得拆单
					Takeout takeoutSpilt = takeoutService.getById(dispactAddress.getTableId());
					takeoutSpilt.setSchedulingStatus(scheduling_status_yes);
					takeoutSpilt.setStatus(modify_status_lock);
					// 锁定单据
					takeoutService.updateById(takeoutSpilt);
					dispactAddressService.updateById(dispactAddress); // 拆单后已经有该条记录，分配给dispactId便可
				default:
					break;
				}
			}
		}
		return bf.toString();
	}

	@Override
	public Dispatch getDispatchById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Dispatchs", allEntries = true)
	public void updateDispatch(Dispatch dispatch) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		dispatch.setStatus(modify_status_revocation);
		baseMapper.updateById(dispatch);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Dispatchs", allEntries = true)
	public void deleteDispatch(Dispatch dispatch) {
		dispatch.setDelFlag(true);
		baseMapper.updateById(dispatch);
	}

	@Override
	@Cacheable("Dispatchs")
	public List<Dispatch> selectAll() {
		QueryWrapper<Dispatch> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public Dispatch backDispatch(String id) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		Dispatch entity = getById(id);
		entity.setStatus(modify_status_await);
		baseMapper.updateById(entity);
		return entity;
	}
	

}
