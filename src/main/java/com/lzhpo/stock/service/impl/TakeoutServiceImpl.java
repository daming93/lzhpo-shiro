package com.lzhpo.stock.service.impl;

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
import com.lzhpo.client.service.IBasicdataService;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.deliver.service.IAddressService;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.ReceiptBill;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.entity.TakeoutDetail;
import com.lzhpo.stock.entity.TakeoutOperations;
import com.lzhpo.stock.mapper.TakeoutMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IReceiptBillService;
import com.lzhpo.stock.service.IStorageService;
import com.lzhpo.stock.service.ITakeoutDetailService;
import com.lzhpo.stock.service.ITakeoutOperationsService;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.sys.service.IGenerateNoService;

import cn.hutool.core.lang.UUID;

/**
 * <p>
 * 出库表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-15
 */
@Service
public class TakeoutServiceImpl extends ServiceImpl<TakeoutMapper, Takeout> implements ITakeoutService {
	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private ITakeoutDetailService takeoutDetailService;
	@Autowired
	private ITakeoutOperationsService operationService;
	@Autowired
	private IMaterialService materialSerivice;
	@Autowired
	private IMaterialOperationsService materialOperationsService;
	@Autowired
	private IStorageService storageService;
	@Autowired
	private IIncomeService incomeService;
	@Autowired
	private IMaterialDepotService materialDepotService;
	@Autowired
	private IClientitemService clientitemService;
	@Autowired
	private IReceiptBillService receiptBillService;
	@Autowired
	private IBasicdataService basicdateService;
	@Autowired
	private IAddressService addressService;
	
	@Override
	public long getTakeoutCount(String name) {
		QueryWrapper<Takeout> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Takeouts", allEntries = true)
	public Takeout saveTakeout(Takeout takeout) {
		Integer type_new = CacheUtils.keyDict.get("stock_type_new").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		//新建出库
		Integer trunover_type_takeout_new = CacheUtils.keyDict.get("trunover_type_takeout_new").getValue();
		
		//scheduling_status_no 调度未排单
		Integer scheduling_status_no = CacheUtils.keyDict.get("scheduling_status_no").getValue();
		takeout.setNumber(null);
		takeout.setStatus(modify_status_await);
		takeout.setCode(generateNoService.nextCode("CK"));
		takeout.setSchedulingStatus(scheduling_status_no);
		baseMapper.insert(takeout);

		List<TakeoutDetail> detailSet = takeout.getDetailSet();

		for (TakeoutDetail takeoutDetail : detailSet) {
			// 根据品项找到物料对应的信息
			// Clientitem item =
			// itemSeivice.getClientitemById(takeoutDetail.getItemId());
			takeoutDetail.setTakeoutId(takeout.getId());
			takeoutDetail.setClientId(takeout.getClientId());
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(takeout.getCode());
			materialOperations.setFromType(trunover_type_takeout_new);// 新建出库
			materialOperations.setMaterialId(takeoutDetail.getMaterial());
			materialOperations.setNumber(takeoutDetail.getNumber());
			materialOperations.setWholeNum(takeoutDetail.getWholeNum());//整库存
			materialOperations.setScatteredNum(takeoutDetail.getScatteredNum());//零库存
			materialOperations.setType(2);// 出库为-
			materialOperationsService.save(materialOperations);
			// 应该在这里把可用库存改成锁定库存 这里也是做库存得二次认证
			// 有materialId使用
			try {
				//锁定物料这个接口也要改 20200722 xudm//
				List<MaterialDepot> mdepotList =  materialSerivice.lockMaterial(takeoutDetail.getMaterial(), takeoutDetail.getNumber(),takeoutDetail.getWholeNum(),takeoutDetail.getScatteredNum(),null);
				for (MaterialDepot materialDepot : mdepotList) {
					takeoutDetail.setDepot(materialDepot.getDepotId());	
					takeoutDetail.setNumber(materialDepot.getNumber());
					takeoutDetail.setWholeNum(materialDepot.getWholeNum());
					takeoutDetail.setScatteredNum(materialDepot.getScatteredNum());
					takeoutDetail.setId(UUID.randomUUID().toString());
					takeoutDetail.setSplit(materialDepot.getSpilt());
					takeoutDetailService.save(takeoutDetail);
				}
			} catch (RuntimeJsonMappingException e) {
				throw new RuntimeJsonMappingException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			//锁入库单
			storageService.lockStorage(takeoutDetail.getMaterial());
		}

		TakeoutOperations operations = new TakeoutOperations();
		operations.setTakeoutId(takeout.getId());
		operations.setType(type_new);
		operations.setOperationId(takeout.getId());// 新建出库的时候操作就是出库单id
		operationService.save(operations);

		return takeout;
	}

	@Override
	public Takeout getTakeoutById(String id) {
		Takeout r = baseMapper.selectById(id);
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
	@CacheEvict(value = "Takeouts", allEntries = true)
	public void updateTakeout(Takeout takeout) {
		//未拣货
		Integer is_exsit_pick_no = CacheUtils.keyDict.get("is_exsit_pick_no").getValue();
		
		//确定
		Integer stock_type_sure = CacheUtils.keyDict.get("stock_type_sure").getValue();
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		takeout.setStatus(modify_status_revocation);
		// sql查询出合计数据
		MathStockNumber math = baseMapper.selectMathTakeoutNumberByTakeoutId(takeout.getId());
		takeout.setVolume(math.getVolumeSum());
		takeout.setWeight(math.getWeightSum());
		takeout.setTotal(math.getNumZ());
		takeout.setTrayNumber(math.getTray());
		takeout.setNumber(math.getNumber());
		takeout.setScatteredNum(math.getScatteredNum());
		takeout.setPickingStatus(is_exsit_pick_no);
		TakeoutOperations operations = new TakeoutOperations();
		operations.setTakeoutId(takeout.getId());
		operations.setType(stock_type_sure);
		operations.setOperationId(takeout.getId());// 新建出库的时候操作就是出库单id
		operationService.save(operations);
		//调账
		Integer transportation_type_adjustment =  CacheUtils.keyDict.get("transportation_type_adjustment").getValue();
		if(!transportation_type_adjustment.equals(takeout.getAdjustment())){//不是调账就计算费用
			//调账就不用生成拣货单和 不拣货也不产生费用
		}else{
			//生成拣货单得code
			takeout.setPickingCode(generateNoService.nextCode("JH"));
		}
		baseMapper.updateById(takeout);
		/**
		 * 预留编辑代码
		 */
		//生成回单 出库单确认得时候应该生成回单 
		ReceiptBill receiptBill = new ReceiptBill();
		receiptBill.setRefId(takeout.getId());
		receiptBill.setIsExistSlip(0);//送货单 这个时候仅有送货单
		receiptBill.setIsExistReceipt(1);//验收单 无
		receiptBill.setIsExistBack(1);//退单 无
		receiptBill.setReceiptStatus(0);//等待路单进行更改其他回单状态
		//在这生成得暂且有以上信息
		receiptBillService.save(receiptBill);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Takeouts", allEntries = true)
	public void deleteTakeout(Takeout takeoutWeb) {
		//撤销出库
		Integer trunover_type_takeout_back = CacheUtils.keyDict.get("trunover_type_takeout_back").getValue();
		//拆零
		Integer takeout_detail_split_yes = CacheUtils.keyDict.get("takeout_detail_split_yes").getValue();
		Takeout takeout = getTakeoutById(takeoutWeb.getId());
		List<TakeoutDetail> detailSet = takeoutDetailService.selecttakeoutDetailBytakeoutId(takeoutWeb.getId());
		for (TakeoutDetail takeoutDetail : detailSet) {
			//看有没有拆单，有拆单就零整转换一下
			if(takeout_detail_split_yes.equals(takeoutDetail.getSplit())){
				takeoutDetail.setWholeNum(takeoutDetail.getWholeNum()+1);//返回拆零，就是把（拆零只会拆一箱零数）把一个转换率的零数转成一个整数
				takeoutDetail.setScatteredNum(takeoutDetail.getScatteredNum()-clientitemService.getById(takeoutDetail.getItemId()).getUnitRate());
			}
			// 先加出库存
			materialSerivice.unlockMaterial(takeoutDetail.getMaterial(), takeoutDetail.getWholeNum(),takeoutDetail.getScatteredNum(),
					clientitemService.getById(takeoutDetail.getItemId()).getUnitRate());
			// 储位的库存对应关系表也要处理
			materialDepotService.mathNumberBymaterialIdAndDepotId(takeoutDetail.getMaterial(), takeoutDetail.getDepot(),
					takeoutDetail.getNumber(),takeoutDetail.getWholeNum(),takeoutDetail.getScatteredNum(), true);
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(takeout.getCode());
			materialOperations.setFromType(trunover_type_takeout_back);// 出库
			materialOperations.setMaterialId(takeoutDetail.getMaterial());
			materialOperations.setNumber(takeoutDetail.getNumber());
			materialOperations.setWholeNum(takeoutDetail.getWholeNum());//整库存
			materialOperations.setScatteredNum(takeoutDetail.getScatteredNum());//零库存
			materialOperations.setType(1);// 出库撤销为+
			materialOperationsService.save(materialOperations);
		}
		takeout.setDelFlag(true);
		baseMapper.updateById(takeout);
		//删除回单
		receiptBillService.deleteReceiptBillByTakeoutId(takeout.getId());
	}

	@Override
	@Cacheable("Takeouts")
	public List<Takeout> selectAll() {
		QueryWrapper<Takeout> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void backTakeout(String id) {
		// 撤回单据就是来一波和确认单据相反得操作
		Integer stock_type_back = CacheUtils.keyDict.get("stock_type_back").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		Takeout takeout = getTakeoutById(id);
		// 记录人得操作
		TakeoutOperations operations = new TakeoutOperations();
		operations.setTakeoutId(takeout.getId());
		operations.setType(stock_type_back);
		operations.setOperationId(takeout.getId());// 撤销出库的时候操作就是出库单id
		operationService.save(operations);
		takeout.setStatus(modify_status_await);
		// 还有计算入库装卸费  撤销该费用
		if(incomeService.getById(takeout)!=null){
			incomeService.deleteIncome(incomeService.getById(takeout));//有输入就删除没有就不用管
		}
		takeout.setIncomeId("无");
		baseMapper.updateById(takeout);
		
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void ensurePick(String id) {
		// 已拣货
		Integer is_exsit_pick_yes = CacheUtils.keyDict.get("is_exsit_pick_yes").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		// 拣货动作
		Integer stock_type_pick = CacheUtils.keyDict.get("stock_type_pick").getValue();
		
		Takeout takeout = getTakeoutById(id);
		takeout.setPickingStatus(is_exsit_pick_yes);
		takeout.setStatus(modify_status_lock);
		//算出库装卸费
		try {
			incomeService.takeoutIncomeMath(takeout);
		}catch (RuntimeJsonMappingException e) {
			throw new RuntimeJsonMappingException(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		updateById(takeout);
		//记录操作
		operationService.saveOpByIdAndType(takeout.getId(), stock_type_pick, takeout.getPickingCode());
	}

	@Override
	public List<Takeout> selectAllByDispatchIds(Set<String> dispatchIds) {
		return baseMapper.selectAllByDispatchIds(dispatchIds);
	}

}
