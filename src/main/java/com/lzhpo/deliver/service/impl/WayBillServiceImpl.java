package com.lzhpo.deliver.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.lzhpo.deliver.entity.WayBill;
import com.lzhpo.deliver.mapper.WayBillMapper;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.deliver.service.IWayBillService;
import com.lzhpo.sys.service.IGenerateNoService;
/**
 * <p>
 * 录单(和计划表基本一样，是计划表得主表，和统计内容) 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-10-14
 */
@Service
public class WayBillServiceImpl extends ServiceImpl<WayBillMapper, WayBill> implements IWayBillService {
	@Autowired
	private IDispatchService dispatchService ;
	
	@Autowired
	private IGenerateNoService generateNoService;
	
	@Autowired
	private IDispactAddressService dispactAddressService ;
	@Override
    public long getWayBillCount(String name) {
        QueryWrapper<WayBill> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "WayBills", allEntries = true)
    public WayBill saveWayBill( List<Dispatch> dispatchs) {
    	WayBill wayBill =  new WayBill();
    	//现在我们需要知道得是很多总和 和 平均值
    	//size vehicleNum
    	int vehicleNum = dispatchs.size();
    	//体积重量  以下为合计
    	BigDecimal volume = new BigDecimal(0);
    	BigDecimal weight = new BigDecimal(0);
    	BigDecimal lodaRate = new BigDecimal(0);
    	BigDecimal weightRate = new BigDecimal(0);
    	for (Dispatch dispatch : dispatchs) {
    		dispatch = dispatchService.getById(dispatch.getId());
    		//从数据库取出得最新数据
    		volume = volume.add(dispatch.getDispatchVolume());
    		weight = weight.add(dispatch.getDispatchWeight());
    		lodaRate = lodaRate.add(dispatch.getLodaRate());
    		weightRate = weightRate.add(dispatch.getWeightRate());
		}
    	wayBill.setCode(generateNoService.nextCode("LD"));
    	wayBill.setVehicleNum(vehicleNum);
    	wayBill.setDispatchVolume(volume);
    	wayBill.setDispatchWeight(weight);
    	wayBill.setLodaRate(lodaRate.divide(new BigDecimal(vehicleNum)));
    	wayBill.setWeightRate(weightRate.divide(new BigDecimal(vehicleNum)));
    	baseMapper.insert(wayBill);
    	for (Dispatch dispatch : dispatchs) {//这一次改变他得路单状态
    		dispatch.setWayBillId(wayBill.getId());
    		//是否排单得状态要不要进行改变 其实绑定了id 就是排单得状态 在列表页表是可以分辨出来
    		//已排单
    		Integer scheduling_status_yes = CacheUtils.keyDict.get("scheduling_status_yes").getValue();
    		//还是给个状态值
    		dispatch.setDispatchStatus(scheduling_status_yes);
    		dispatchService.updateById(dispatch);
		}
        return wayBill;
    }

    @Override
    public WayBill getWayBillById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "WayBills", allEntries = true)
    public void updateWayBill(WayBill wayBill) {
        baseMapper.updateById(wayBill);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "WayBills", allEntries = true)
    public void deleteWayBill(WayBill wayBill) {
        wayBill.setDelFlag(true);
        baseMapper.updateById(wayBill);
        //然后将子表得关联关系删除
        dispatchService.backDispatchByWaybillId(wayBill.getId());
    }

    @Override
    @Cacheable("WayBills")
    public List<WayBill> selectAll() {
        QueryWrapper<WayBill> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public int verifyWayBill(List<Dispatch> dispatchs) {
		//已排单
		Integer scheduling_status_yes = CacheUtils.keyDict.get("scheduling_status_yes").getValue();
		Map<String, Long> map = new HashMap<String, Long>();
		for (Dispatch dispatch : dispatchs) {
    		dispatch = dispatchService.getById(dispatch.getId());
    		if(dispatch.getDispatchStatus()!=null&&dispatch.getDispatchStatus().equals(scheduling_status_yes)){
    			return 1;//状态码1 说明有绑定得单据了
    		}
    		//然后查询对应得单据表
    		List<DispactAddress> adressList = dispactAddressService.getListByDispatchId(dispatch.getId());
    		for (DispactAddress dispactAddress : adressList) {
    			 //先统计会好计算
    			if(map.containsKey(dispactAddress.getTableId())){//包含就＋1
    				map.replace(dispactAddress.getTableId(), map.get(dispactAddress.getTableId())+1);
    			}else{
    				map.put(dispactAddress.getTableId(), 1l);
    			}
			}
		}
		for (Dispatch dispatch : dispatchs) {
    		dispatch = dispatchService.getById(dispatch.getId());
    		//然后查询对应得单据表
    		List<DispactAddress> adressList = dispactAddressService.getListByDispatchId(dispatch.getId());
    		for (DispactAddress dispactAddress : adressList) {
    			 //先统计会好计算
    			Long count = dispactAddressService.countDetailByTableId(dispactAddress.getTableId());//这个数字是所有拆单 也就是 多个配送计划 要合并计算
    			Long countDetail = map.get(dispactAddress.getTableId());
    			if(count.compareTo(countDetail)!=0){//只要不相等 就不是返回状态码2 
    				return 2;//说明没有收集全所有得拆单
    			}
			}
		}	
		//把其中得每一条记录都要把所有单据 轮查一遍
		return 0;//就正常
	}


}
