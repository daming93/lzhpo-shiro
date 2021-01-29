package com.lzhpo.deliver.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.deliver.entity.DispactAddress;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.DispatchCost;
import com.lzhpo.deliver.entity.ExpressBill;
import com.lzhpo.deliver.entity.VehicleContractMain;
import com.lzhpo.deliver.entity.VehicleContractMainDetail;
import com.lzhpo.deliver.mapper.DispatchMapper;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IDispatchCostService;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.deliver.service.IExpressBillService;
import com.lzhpo.deliver.service.IVehicleContractMainDetailService;
import com.lzhpo.deliver.service.IVehicleContractMainService;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.ILineTakeoutService;
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

	@Autowired
	private ILineTakeoutService lineTakeoutService;
	
	@Autowired
	private IVehicleContractMainService vehicleContractMainService;
	
	@Autowired
	private IVehicleContractMainDetailService vehicleContractMainDetailService ;
	
	@Autowired
	private IDispatchCostService dispatchCostService;
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
		//未排单
		Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
		dispatch.setDispatchStatus(scheduling_status_no);
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
				case 5:// 线路发单 得表 把其配送状态改为已排单
					LineTakeout linetakeout = lineTakeoutService.getById(dispactAddress.getTableId());
					linetakeout.setSchedulingStatus(scheduling_status_yes);
					linetakeout.setStatus(modify_status_lock);
					// 锁定单据
					lineTakeoutService.updateById(linetakeout);
					dispactAddress.setId(null);
					dispactAddressService.save(dispactAddress);
					break;
				case 6: // 线路发单 得拆单
					LineTakeout linetakeoutSplit = lineTakeoutService.getById(dispactAddress.getTableId());
					linetakeoutSplit.setSchedulingStatus(scheduling_status_yes);
					linetakeoutSplit.setStatus(modify_status_lock);
					// 锁定单据
					lineTakeoutService.updateById(linetakeoutSplit);
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
		
		//获得他的子表，去生成单车的运费 支出费用
		List<DispactAddress> list = dispactAddressService.countNumDetailSendAreaByDispatchId(dispatch.getId());
		//找到他的点数
		int pointNum =(int) dispactAddressService.countNumDetailSendPlaceByDispatchId(dispatch.getId());
		List<BigDecimal> moneyList = new ArrayList<BigDecimal>();
		DispatchCost cost = new DispatchCost();
		//找到这个车辆得合同
		VehicleContractMain main = vehicleContractMainService.getByVehicleId(dispatch.getVehicleId());
		if(main==null){
			throw new RuntimeJsonMappingException("该车辆未找到对应合同");
		}
		cost.setPointNum(pointNum);
		//难点费暂且不算
		cost.setMinPoint(main.getMinPoint());
		cost.setPointPrice(main.getPointPrice());
		
		for (DispactAddress dispactAddress : list) {
			VehicleContractMainDetail detail = vehicleContractMainDetailService.selectDetailMoneyByInfoNoRange(main.getId(), dispactAddress.getProvinceId(), dispactAddress.getCityId(), dispactAddress.getCountiesId());
			if(detail!=null){
				moneyList.add(detail.getMoney());
			}
		}
		//从List中找到最大值
		BigDecimal money = Collections.max(moneyList);
		//区域收费 然后看点数到没有
		if(money==null){
			money = new BigDecimal(0);
		}
		if(pointNum>=main.getMinPoint()){
			//大于等于起送量 也就是从这个开始计费
			Integer num = pointNum-main.getMinPoint()+1;
			//支出等于基础运费加上额外点数*点费
			cost.setMoeny(money.add(main.getPointPrice().multiply(new BigDecimal(num))));
		}else{
			cost.setMoeny(money);
		}
		dispatchCostService.save(cost);
		baseMapper.updateById(dispatch);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Dispatchs", allEntries = true)
	public void deleteDispatch(Dispatch dispatch) {
		dispatch.setDelFlag(true);
		dispatchCostService.deleteByDispatchId(dispatch.getId());
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

	@Override
	public int backDispatchByWaybillId(String wayBillId) {
		QueryWrapper<Dispatch> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("way_bill_id", wayBillId);
		Dispatch entity = new Dispatch();
		entity.setWayBillId("");
		//未排单
		Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
		entity.setDispatchStatus(scheduling_status_no);
		baseMapper.update(entity, wrapper);
		return 0;
	}

	@Override
	public List<Dispatch> selectByWayBillId(String wayBillId) {
		QueryWrapper<Dispatch> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.eq("way_bill_id", wayBillId);
		return baseMapper.selectList(wrapper);
	}
	

}
