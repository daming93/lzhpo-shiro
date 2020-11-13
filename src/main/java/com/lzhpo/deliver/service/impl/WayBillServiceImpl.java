package com.lzhpo.deliver.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.entity.DeliverContractMain;
import com.lzhpo.client.entity.DeliverContractMainDetail;
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.client.service.IDeliverContractMainDetailService;
import com.lzhpo.client.service.IDeliverContractMainService;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.deliver.entity.Address;
import com.lzhpo.deliver.entity.DispactAddress;
import com.lzhpo.deliver.entity.Dispatch;
import com.lzhpo.deliver.entity.WayBill;
import com.lzhpo.deliver.mapper.WayBillMapper;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.deliver.service.IDispactAddressService;
import com.lzhpo.deliver.service.IDispatchService;
import com.lzhpo.deliver.service.IWayBillService;
import com.lzhpo.finance.entity.Income;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.service.ITakeoutService;
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

	@Autowired
	private ITakeoutService takeoutService ;
	
	@Autowired
	private IAddressService addressService;
	
	@Autowired
	private IDeliverContractMainService deliverContractMainService ;
	
	@Autowired
	private IDeliverContractMainDetailService deliverContractMainDetailService ;
	
	@Autowired
	private IBasicdataService basicdataService ;
	
	@Autowired
	private IIncomeService incomeService ;
	
	
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
    	String clientDeliverIncome = "4c089061ca5243fd97f02213015d44e9";//客户运费 预设
    	WayBill wayBill =  new WayBill();
    	//现在我们需要知道得是很多总和 和 平均值
    	//size vehicleNum
    	int vehicleNum = dispatchs.size();
    	//体积重量  以下为合计
    	BigDecimal volume = new BigDecimal(0);
    	BigDecimal weight = new BigDecimal(0);
    	BigDecimal lodaRate = new BigDecimal(0);
    	BigDecimal weightRate = new BigDecimal(0);
    	Set<String> tableIds = new 	HashSet<>();//所有出库单的id
    	for (Dispatch dispatch : dispatchs) {
    		dispatch = dispatchService.getById(dispatch.getId());
    		//从数据库取出得最新数据
    		volume = volume.add(dispatch.getDispatchVolume());
    		weight = weight.add(dispatch.getDispatchWeight());
    		lodaRate = lodaRate.add(dispatch.getLodaRate());
    		weightRate = weightRate.add(dispatch.getWeightRate());
    		//找对应的出库单
    		//然后查询对应得单据表
    		List<DispactAddress> adressList = dispactAddressService.getListByDispatchId(dispatch.getId());
    		for (DispactAddress dispactAddress : adressList) {
    			if(dispactAddress.getType()==2||dispactAddress.getType()==4){
    				//对应的tableid
    				tableIds.add(dispactAddress.getTableId());//找出对应的出库单的id
    			}
			}
		}
    	wayBill.setCode(generateNoService.nextCode("LD"));
    	wayBill.setVehicleNum(vehicleNum);
    	wayBill.setDispatchVolume(volume);
    	wayBill.setDispatchWeight(weight);
    	wayBill.setLodaRate(lodaRate.divide(new BigDecimal(vehicleNum)));
    	wayBill.setWeightRate(weightRate.divide(new BigDecimal(vehicleNum)));
    	//插入路单前，需要在这里计算费用
    	//计算过程现在不清楚
    	//按客户收费 同一个路单 从合同里找他的计费方式  无论如何可以统计出来一个客户的体积，重量，货值，运输方式也要算进去
    	//（在某个路单）某个客户  配送时间 配送方式  送什么区域 送了 多少（方，中，货值）
    	//以上信息可以分类出来
    	//此时路单还没有生成，可以由配送计划来看是否可以（如果没有区域报价怎么办）（就是值配送的区间内是没有的）(没有区间不准路单，在录入合同的阶段就应该做好提醒）
    	//有所有单据之后按时间之类的分
    	//预设收入
    	List<Income> incomeList = new ArrayList<Income>();
    	List<Takeout> takeoutList = takeoutService.selectAllByDispatchIds(tableIds);
    	for (Takeout takeout : takeoutList) {//这里 总数和零数可能之前计算有问题 
			//这里就是按五个同一原则分配出来的金额
    		String clientId = takeout.getClientId();//有客户之后//找客户的合同
    		Basicdata client = basicdataService.getBasicdataById(clientId);
    		//找到客户正在使用的配送合同 其中有地址信息 地址信息去找省市区
    		String addressId = takeout.getAddressId();
    		Address address = addressService.getById(addressId);
    		String main = deliverContractMainService.getUsingContractId(clientId);//正在使用的合同id
    		DeliverContractMain mainContract = deliverContractMainService.getById(main);
    		//由于不知道采取的是什么类型的计费方式，所以我们要把重件方货值都拿出来去找，找到为止，找不到提醒用户无改范围的报价
    		//件数
    		Integer freight_type_number =  CacheUtils.keyDict.get("freight_type_number").getValue();
    		
    		DeliverContractMainDetail detail = deliverContractMainDetailService//这个range 是要求类型的 type //件数先按总数来
    				.selectDetailMoneyByInfo(main, address.getProvinceId(), address.getCityId(), address.getCountiesId(), takeout.getTotal(),freight_type_number);
    		Integer freight_type_volume =  CacheUtils.keyDict.get("freight_type_volume").getValue();
    		Integer freight_type_weight =  CacheUtils.keyDict.get("freight_type_weight").getValue();
    		Integer freight_type_salesvolume =  CacheUtils.keyDict.get("freight_type_salesvolume").getValue();
    		if(detail==null){//没找到件数 找体积
    			detail = deliverContractMainDetailService
        				.selectDetailMoneyByInfo(main, address.getProvinceId(), address.getCityId(), address.getCountiesId(), takeout.getVolume(),freight_type_volume);
    		}
    		if(detail==null){//没找到体积 找重量
    			detail = deliverContractMainDetailService
        				.selectDetailMoneyByInfo(main, address.getProvinceId(), address.getCityId(), address.getCountiesId(), takeout.getWeight(),freight_type_weight);
    		}
    		if(detail==null){//没找到重量 找销售额
    			detail = deliverContractMainDetailService
        				.selectDetailMoneyByInfo(main, address.getProvinceId(), address.getCityId(), address.getCountiesId(), takeout.getMoney(),freight_type_salesvolume);
    		}
    		if(detail==null){
    			throw new RuntimeJsonMappingException("未在"+client.getClientShortName()+"的合同找到该范围的报价");
    		}else{
    			//此时我们得到了他的报价 //要把计费过程记录下来吗
    			Income income = new Income();
    			//什么客户在什么范围内 单价多少 * 单位 == 收费金额 （合同编号） 出库单号 计费方式（重方件钱）
    			Integer type = detail.getType();
    			String tmep = "按";
    			BigDecimal sum = new BigDecimal(1);
    			//还要乘以系数 //就是看配送方式
    			Integer tranType = takeout.getTransportationType();
    			BigDecimal coefficient = new BigDecimal(0);//系数
    			switch (tranType) {
				case 2://加急
					coefficient = mainContract.getTransportationTypeUrgent();
					break;
				case 3://节日
					coefficient = mainContract.getTransportationTypeHoliday();
					break;
				case 6://托运
					coefficient = mainContract.getTransportationTypeConsign();
					break;
				default:
					break;
				}
    			switch (type) {
				case 1://件数
					sum = detail.getMoney().multiply(new BigDecimal(takeout.getTotal())).multiply(coefficient);
					tmep=tmep+"件数"+"计算过程("+takeout.getTotal()+"*"+detail.getMoney()+"[单价]"+"*"+coefficient+"[系数]="+sum+")收取 "+sum+"(元)" ;
					break;
				case 2://方数
					sum = detail.getMoney().multiply(takeout.getVolume()).multiply(coefficient);
					tmep=tmep+"方数"+"计算过程("+takeout.getVolume()+"*"+detail.getMoney()+"[单价]"+"*"+coefficient+"[系数]="+sum+")收取 "+sum+"(元)";
					break;
				case 3://重量
					sum = detail.getMoney().multiply(takeout.getWeight()).multiply(coefficient);
					tmep=tmep+"重量"+"计算过程("+takeout.getWeight()+"*"+detail.getMoney()+"[单价]"+"*"+coefficient+"[系数]="+sum+")收取 "+sum+"(元)";
					break;
				default://销售额
					sum = detail.getMoney().multiply(takeout.getMoney()).multiply(coefficient);
					tmep=tmep+"销售额"+"计算过程("+takeout.getMoney()+"*"+detail.getMoney()+"[百分比]"+"*"+coefficient+"[系数]="+sum+")收取 "+sum+"(元)";
					break;
				}
    			
    			//如果要记下来以什么形式记下来
    			//所有的都通过才去设置Income
    		
    			income.setTableCode(wayBill.getCode());
    			income.setClientId(clientId);
    			income.setBasis(mainContract.getContractCode());
    			income.setOptionId(clientDeliverIncome);
    			income.setMoeny(sum);
    			income.setBasicId(mainContract.getId());
    			String remark = client.getClientShortName()+"在"+income.getTableCode()+"中"+tmep+"合同编号为"+income.getBasis();
    			income.setRemarks(remark);
    			incomeList.add(income);
    		}
    		
		}
    	baseMapper.insert(wayBill);
    	//算收入 只有全部通过才会计算收入
    	for (Income income : incomeList) {
			income.setTableId(wayBill.getId());
			income.setTableFrom(CacheUtils.keyDict.get("income_from_waybill").getValue());
			income.setCode(generateNoService.nextCode("SR"));
			incomeService.save(income);
		}
    	
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
