package com.lzhpo.stock.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.mapper.LineTakeoutMapper;
import com.lzhpo.stock.service.ILineTakeoutService;
import com.lzhpo.sys.service.IGenerateNoService;

/**
 * <p>
 * 线路发单（无关库存的发单) 表单形式和库存发单接近，但是没有子表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2021-01-25
 */
@Service
public class LineTakeoutServiceImpl extends ServiceImpl<LineTakeoutMapper, LineTakeout> implements ILineTakeoutService {
	@Autowired
	private IIncomeService incomeService;
	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private IBasicdataService basicdateService;
	@Autowired
	private IAddressService addressService;
	@Override
	public long getLineTakeoutCount(String name) {
		QueryWrapper<LineTakeout> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public LineTakeout saveLineTakeout(LineTakeout lineTakeout) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();

		//scheduling_status_no 调度未排单
		Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
		lineTakeout.setStatus(modify_status_await);
		lineTakeout.setCode(generateNoService.nextCode("XL"));//线路出单
		lineTakeout.setSchedulingStatus(scheduling_status_no);
		baseMapper.insert(lineTakeout);
		return lineTakeout;
	}

	@Override
	public LineTakeout getLineTakeoutById(String id) {
		LineTakeout r =  baseMapper.selectById(id);
		if (StringUtils.isNotBlank(r.getClientId())) {
			r.setClientName(basicdateService.getById(r.getClientId()).getClientShortName());
		}
		if (r.getStatus() != null) {
			r.setStatusStr(CommomUtil.valueToNameInDict(r.getStatus(), "modify_status"));
		}
		if (r.getPickingStatus()!= null) {
			r.setPickStatusStr(CommomUtil.valueToNameInDict(r.getPickingStatus(), "is_exsit_pick"));
		}
		if (r.getTransportationType() != null) {
			r.setTransportationTypeStr(CommomUtil.valueToNameInDict(r.getTransportationType(), "transportation_type"));
		}
		if (StringUtils.isNotBlank(r.getAddressId())) {
			r.setAddressName(addressService.getById(r.getAddressId()).getAddressName());
		}
		return r;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public void updateLineTakeout(LineTakeout lineTakeout) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		lineTakeout.setStatus(modify_status_revocation);
		//调账
		Integer transportation_type_adjustment =  CacheUtils.keyDict.get("transportation_type_adjustment").getValue();
		//由于没有拣货单，在这里就要生成出库得装卸费用
		if(!transportation_type_adjustment.equals(lineTakeout.getAdjustment())){//不是调账就计算费用
			//调账就不用生成拣货单和 不拣货也不产生费用
		}else{
			try {
				lineTakeout = incomeService.linetakeoutIncomeMath(lineTakeout);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		baseMapper.updateById(lineTakeout);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "LineTakeouts", allEntries = true)
	public void deleteLineTakeout(LineTakeout lineTakeout) {
		lineTakeout.setDelFlag(true);
		baseMapper.updateById(lineTakeout);
	}

	@Override
	@Cacheable("LineTakeouts")
	public List<LineTakeout> selectAll() {
		QueryWrapper<LineTakeout> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void backTakeout(String id) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		LineTakeout lineTakeout = getLineTakeoutById(id);
		lineTakeout.setStatus(modify_status_await);
		// 还有计算入库装卸费  撤销该费用
		if(incomeService.getById(lineTakeout.getIncomeId())!=null){
			incomeService.deleteIncome(incomeService.getById(lineTakeout.getIncomeId()));//有输入就删除没有就不用管
		}
		lineTakeout.setIncomeId("无");
		baseMapper.updateById(lineTakeout);

	}

}
