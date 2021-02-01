package com.lzhpo.deliver.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.deliver.entity.DispactAddress;
import com.lzhpo.deliver.entity.ExpressBill;
import com.lzhpo.deliver.mapper.DispactAddressMapper;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IExpressBillService;
import com.lzhpo.stock.entity.LineTakeout;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.ILineTakeoutService;
import com.lzhpo.stock.service.ITakeoutService;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-09-03
 */
@Service
public class DispactAddressServiceImpl extends ServiceImpl<DispactAddressMapper, DispactAddress>
		implements IDispactAddressService {

	@Autowired
	private IExpressBillService expressBillService;

	@Autowired
	private ITakeoutService takeoutService;

	@Autowired
	private ILineTakeoutService linetakeoutService;
	@Override
	public long getDispactAddressCount(String code) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("code",code);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DispactAddresss", allEntries = true)
	public DispactAddress saveDispactAddress(DispactAddress dispactAddress) {
		baseMapper.insert(dispactAddress);
		/**
		 * 预留编辑代码
		 */
		return dispactAddress;
	}

	@Override
	public DispactAddress getDispactAddressById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DispactAddresss", allEntries = true)
	public void updateDispactAddress(DispactAddress dispactAddress) {
		baseMapper.updateById(dispactAddress);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DispactAddresss", allEntries = true)
	public void deleteDispactAddress(DispactAddress dispactAddress) {
		dispactAddress.setDelFlag(true);
		baseMapper.updateById(dispactAddress);
	}

	@Override
	@Cacheable("DispactAddresss")
	public List<DispactAddress> selectAll() {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<DispactAddress> getDispactWaitForDeliverBill(Map<String, Object> map) {
		return baseMapper.getDispactWaitForDeliverBill(map);
	}

	@Override
	public String splitBill(DispactAddress dispactAddress) {
		Integer total = dispactAddress.getAdjustmentTotal() == null ? 0 : dispactAddress.getAdjustmentTotal();
		BigDecimal volume = dispactAddress.getAdjustmentVolume() == null ? new BigDecimal(0)
				: dispactAddress.getAdjustmentVolume();
		BigDecimal weight = dispactAddress.getAdjustmentWeight() == null ? new BigDecimal(0)
				: dispactAddress.getAdjustmentWeight();
		// 前台传过来得信息不一定完全
		//根据type不同 找不同得实体
		if(dispactAddress.getType()!=null){
    		if(dispactAddress.getType()==1){//库存发单
    			dispactAddress = getDispactAddressByTakoutId(dispactAddress.getId());// 补全信息
        	}else if(dispactAddress.getType()==3){
        		dispactAddress = getDispactAddressByLineTakoutId(dispactAddress.getId());// 补全信息
        	}else{//快速发单
        		dispactAddress = getDispactAddressByBillId(dispactAddress.getId());// 补全信息
        	}
    	}
		switch (dispactAddress.getType()) {
		case 1: // 快单
			// 要拆单标记下
			ExpressBill expressBill = expressBillService.getById(dispactAddress.getTableId());
			expressBill.setSplit(1);
			expressBillService.updateById(expressBill);
			// 需要拆成两条记录
			// 要改变得就只有 数量体积 重量
			// code要改成拆单得code
			dispactAddress.setType(3);
			dispactAddress.setTotal(dispactAddress.getTotal() - total);
			dispactAddress.setVolume(dispactAddress.getVolume().subtract(volume));
			dispactAddress.setWeight(dispactAddress.getWeight().subtract(weight));
			dispactAddress.setId(null);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			baseMapper.insert(dispactAddress);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			dispactAddress.setId(null);
			dispactAddress.setTotal(total);
			dispactAddress.setVolume(volume);
			dispactAddress.setWeight(weight);
			baseMapper.insert(dispactAddress);
			break;
		case 2: // 库存发单

			Takeout takeout = takeoutService.getById(dispactAddress.getTableId());
			takeout.setSplit(1);// 拆单状态
			takeoutService.updateById(takeout);
			// 需要拆成两条记录
			// 要改变得就只有 数量体积 重量
			dispactAddress.setType(4);
			dispactAddress.setTotal(dispactAddress.getTotal() - total);
			dispactAddress.setVolume(dispactAddress.getVolume().subtract(volume));
			dispactAddress.setWeight(dispactAddress.getWeight().subtract(weight));
			dispactAddress.setId(null);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			baseMapper.insert(dispactAddress);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			dispactAddress.setId(null);
			dispactAddress.setTotal(total);
			dispactAddress.setVolume(volume);
			dispactAddress.setWeight(weight);
			baseMapper.insert(dispactAddress);
			break;
		case 5: // 线路发单

			LineTakeout linetakeout = linetakeoutService.getById(dispactAddress.getTableId());
			linetakeout.setSplit(1);// 拆单状态
			linetakeoutService.updateById(linetakeout);
			// 需要拆成两条记录
			// 要改变得就只有 数量体积 重量
			dispactAddress.setType(6);//线路发单的拆单
			dispactAddress.setTotal(dispactAddress.getTotal() - total);
			dispactAddress.setVolume(dispactAddress.getVolume().subtract(volume));
			dispactAddress.setWeight(dispactAddress.getWeight().subtract(weight));
			dispactAddress.setId(null);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			baseMapper.insert(dispactAddress);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			dispactAddress.setId(null);
			dispactAddress.setTotal(total);
			dispactAddress.setVolume(volume);
			dispactAddress.setWeight(weight);
			baseMapper.insert(dispactAddress);
			break;	
		default: // 其他3，4 ,6是需要update得 更新一条＋一条
			dispactAddress.setTotal(dispactAddress.getTotal() - total);
			dispactAddress.setVolume(dispactAddress.getVolume().subtract(volume));
			dispactAddress.setWeight(dispactAddress.getWeight().subtract(weight));
			baseMapper.updateById(dispactAddress);
			dispactAddress.setTotal(total);
			dispactAddress.setVolume(volume);
			dispactAddress.setWeight(weight);
			dispactAddress.setId(null);
			dispactAddress.setCode(splitCode(dispactAddress.getClientCode(),dispactAddress.getCode()));
			baseMapper.insert(dispactAddress);
			break;
		}
		return null;
	}

	@Override
	public DispactAddress getDispactAddressByBillId(String id) {
		return baseMapper.getDispactAddressByBillId(id);
	}

	@Override
	public String splitCode(String clientCode,String code) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		if(code.contains("-")){
			code = code.substring(code.indexOf("-")+1, code.length());
		}
		wrapper.like("code", code);
		Integer count = baseMapper.selectCount(wrapper)+1;
		return "CD"+count+"-"+code;
	}

	@Override
	public String changeBindingStatus(String disAddressId) {
		DispactAddress dispactAddress = baseMapper.selectById(disAddressId);
		if(dispactAddress!=null){
			//清空绑定态，还要让单据回复到未排单的状态 scheduling_status_no
			// 未排单
			Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
			// 可撤销
			Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
			switch (dispactAddress.getType()) {
			case 1:// 快速发单 得表 把其配送状态改为未排单
				ExpressBill expressBill = expressBillService.getById(dispactAddress.getTableId());
				expressBill.setSchedulingStatus(scheduling_status_no);
				// 撤销锁定单据
				expressBill.setStatus(modify_status_revocation);
				expressBillService.updateById(expressBill);
				deleteDispactAddress(dispactAddress);//删除对应单据
				break;
			case 2:// 库存发单 得表 把其配送状态改为未排单
				Takeout takeout = takeoutService.getById(dispactAddress.getTableId());
				takeout.setSchedulingStatus(scheduling_status_no);
				takeout.setStatus(modify_status_revocation);
				// 撤销锁定单据
				takeoutService.updateById(takeout);
				deleteDispactAddress(dispactAddress);//删除对应单据
				break;

			case 3:// 快速发单得拆单
				dispactAddress.setDispacthId("");
				updateById(dispactAddress);
				break;

			case 4: // 出库单 得拆单
				dispactAddress.setDispacthId("");
				updateById(dispactAddress); // 拆单后已经有该条记录，撤销给dispactId便可
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public String bindingStatus(DispactAddress dispactAddress) {
		// 已排单
		Integer scheduling_status_yes = CacheUtils.keyDict.get("scheduling_status_yes").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		//这里处理单据id为空的单据
		//跳过dispatchId不为空的单据
		//dispactAddress.setDispacthId(dispatch.getId()); 在前台组装好对应的id
		// 原表更改它得配送状态
		//先验证是否已经排过了 防止停留页面时候被第三人排单
		String code = dispactAddress.getCode();
		String dispacthId = dispactAddress.getDispacthId();
		long count = getCountByCodeAndIDispacthId(code, dispacthId);
		if(count>0){//已经绑定过
			return code + "该单已排过";
		}
		switch (dispactAddress.getType()) {
		case 1:// 快速发单 得表 把其配送状态改为已排单
			ExpressBill expressBill = expressBillService.getById(dispactAddress.getTableId());
			expressBill.setSchedulingStatus(scheduling_status_yes);
			// 锁定单据
			expressBill.setStatus(modify_status_lock);
			expressBillService.updateById(expressBill);
			dispactAddress.setId(null);
			save(dispactAddress);
			break;
		case 2:// 库存发单 得表 把其配送状态改为已排单
			Takeout takeout = takeoutService.getById(dispactAddress.getTableId());
			takeout.setSchedulingStatus(scheduling_status_yes);
			takeout.setStatus(modify_status_lock);
			// 锁定单据
			takeoutService.updateById(takeout);
			dispactAddress.setId(null);
			save(dispactAddress);
			break;

		case 3:// 快速发单得拆单
			ExpressBill expressBillSpilt = expressBillService.getById(dispactAddress.getTableId());
			expressBillSpilt.setSchedulingStatus(scheduling_status_yes);
			// 锁定单据
			expressBillSpilt.setStatus(modify_status_lock);
			expressBillService.updateById(expressBillSpilt);
			updateById(dispactAddress);
			break;

		case 4: // 出库单 得拆单
			Takeout takeoutSpilt = takeoutService.getById(dispactAddress.getTableId());
			takeoutSpilt.setSchedulingStatus(scheduling_status_yes);
			takeoutSpilt.setStatus(modify_status_lock);
			// 锁定单据
			takeoutService.updateById(takeoutSpilt);
			updateById(dispactAddress); // 拆单后已经有该条记录，分配给dispactId便可
		default:
			break;
		}
		return "排单成功";
	}

	public long getCountByCodeAndIDispacthId(String code, String dispacthId) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("code",code);
		wrapper.eq("dispacth_id", dispacthId);
		long count = baseMapper.selectCount(wrapper);
		return count;
	}

	@Override
	public List<DispactAddress> getDispactWaitForTakeoutBill(Map<String, Object> map) {
		return baseMapper.getDispactWaitForTakoutBill(map);
	}

	@Override
	public DispactAddress getDispactAddressByTakoutId(String id) {
		return baseMapper.getDispactAddressByTakoutId(id);
	}

	@Override
	public List<DispactAddress> getListByDispatchId(String dispacthId) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("dispacth_id",dispacthId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public long countDetailByTableId(String tableId) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		wrapper.eq("table_id",tableId);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	public int countNumDetailSendPlaceByDispatchId(String dispatchId) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		wrapper.eq("dispacth_id", dispatchId).groupBy("counties_id","area_name");
		return baseMapper.selectList(wrapper).size();
	}

	@Override
	public List<DispactAddress> countNumDetailSendAreaByDispatchId(String dispatchId) {
		QueryWrapper<DispactAddress> wrapper = new QueryWrapper<>();
		wrapper.eq("dispacth_id", dispatchId).groupBy("counties_id");
		return baseMapper.selectList(wrapper);
	}

	@Override
	public List<DispactAddress> getDispactWaitForLineTakeoutBill(Map<String, Object> map) {
		return baseMapper.getDispactWaitForLineBill(map);
	}

	@Override
	public DispactAddress getDispactAddressByLineTakoutId(String id) {
		return baseMapper.getDispactAddressByLineTakoutId(id);
	}
}
