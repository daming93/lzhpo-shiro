package com.lzhpo.stock.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.finance.service.IIncomeService;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.DirectReturn;
import com.lzhpo.stock.entity.DirectReturnDetail;
import com.lzhpo.stock.entity.DirectReturnOperations;
import com.lzhpo.stock.mapper.DirectReturnMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IMaterialTrayService;
import com.lzhpo.stock.service.IDirectReturnDetailService;
import com.lzhpo.stock.service.IDirectReturnOperationsService;
import com.lzhpo.stock.service.IDirectReturnService;
import com.lzhpo.stock.service.IHandleAbnormityService;
import com.lzhpo.sys.service.IGenerateNoService;

/**
 * <p>
 * 退货表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class DirectReturnServiceImpl extends ServiceImpl<DirectReturnMapper, DirectReturn> implements IDirectReturnService {

	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private IDirectReturnDetailService DirectReturnDetailService;
	@Autowired
	private IDirectReturnOperationsService DirectReturnOperationsService;
	@Autowired
	private IMaterialService materialService;

	@Autowired
	private IMaterialDepotService materialDepotService;

	@Autowired
	private IMaterialTrayService materialTrayService;

	@Autowired
	private IMaterialOperationsService materialOperationsService;
	@Autowired
	private IIncomeService incomeService;
	@Autowired
	private IHandleAbnormityService handleAbnormityService;
	
	@Override
	public long getDirectReturnCount(String name) {
		QueryWrapper<DirectReturn> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturns", allEntries = true)
	public DirectReturn saveDirectReturn(DirectReturn DirectReturn) {
		Integer type_new = CacheUtils.keyDict.get("stock_type_new").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		DirectReturn.setNumber(null);// 确定的时候在核对数量
		DirectReturn.setStatus(modify_status_await);
		// 入库单号
		DirectReturn.setSystemCode(generateNoService.nextCode("ZT"));
		baseMapper.insert(DirectReturn);
		List<DirectReturnDetail> detail = DirectReturn.getDetailSet();
		for (DirectReturnDetail DirectReturnDetail : detail) {
			if(DirectReturnDetail.getNumber()!=null&&DirectReturnDetail.getNumber().equals(0)){
				continue;//总数为0的不需要生成明细
			}
			DirectReturnDetail.setDirectReturnId(DirectReturn.getId());
			DirectReturnDetailService.save(DirectReturnDetail);
		}
		// 记录操作
		DirectReturnOperations operations = new DirectReturnOperations();
		operations.setReturnId(DirectReturn.getId());
		operations.setType(type_new);
		operations.setOperationId(DirectReturn.getId());// 新建入库的时候操作就是入库单id
		DirectReturnOperationsService.save(operations);
		return DirectReturn;
	}

	@Override
	public DirectReturn getDirectReturnById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturns", allEntries = true)
	public void updateDirectReturn(DirectReturn DirectReturn) {
		// 新建销退
		Integer trunover_type_direct_return_new = CacheUtils.keyDict.get("trunover_type_direct_return_new").getValue();

		Integer stock_type_sure = CacheUtils.keyDict.get("stock_type_sure").getValue();

		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();

		// 不良品
		Integer material_type_rejects = CacheUtils.keyDict.get("material_type_rejects").getValue();

		DirectReturn.setStatus(modify_status_revocation);
		// 删除有关子表
		DirectReturnDetailService.deleteDirectReturnDetailByReturnId(DirectReturn.getId());
		List<DirectReturnDetail> detail = DirectReturn.getDetailSet();
		for (DirectReturnDetail DirectReturnDetail : detail) {
			// 根据品项找到物料对应的信息
			// 先加入库存
			Material material = materialService.getMaterialByItemId(DirectReturnDetail.getItemId(),
					DirectReturnDetail.getBatch(), material_type_rejects);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() + DirectReturnDetail.getNumber());// 增加可用库存
				material.setDepotCode(material.getDepotCode() + DirectReturnDetail.getNumber());// 增加库存
				material.setWholeNum(material.getWholeNum() + DirectReturnDetail.getWholeNum());//增加整库存
				material.setScatteredNum(material.getScatteredNum() + DirectReturnDetail.getScatteredNum());//增加零库存
				materialService.updateById(material);
			} else {
				material = new Material();
				material.setDelFlag(false);
				material.setAvailableNum(DirectReturnDetail.getNumber());// 增加可用库存
				material.setDepotCode(DirectReturnDetail.getNumber());// 增加库存
				material.setWholeNum(DirectReturnDetail.getWholeNum());//增加整库存
				material.setScatteredNum(DirectReturnDetail.getScatteredNum());//增加零库存
				material.setItemId(DirectReturnDetail.getItemId());
				material.setBatchNumber(DirectReturnDetail.getBatch());
				material.setClientId(DirectReturn.getClientId());
				material.setLockCode(0);
				material.setType(material_type_rejects);// 不良品
				materialService.save(material);
			}

			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), DirectReturnDetail.getDepot(),
					DirectReturnDetail.getNumber(),DirectReturnDetail.getWholeNum(),DirectReturnDetail.getScatteredNum(), true);
			if (StringUtils.checkValNotNull(DirectReturnDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), DirectReturnDetail.getTray(),
						DirectReturnDetail.getNumber(), true);
			}
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(DirectReturn.getSystemCode());
			materialOperations.setFromType(trunover_type_direct_return_new);// 入库
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(DirectReturnDetail.getNumber());
			materialOperations.setWholeNum(DirectReturnDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(DirectReturnDetail.getScatteredNum());//增加零库存
			materialOperations.setType(1);// 入库为＋
			materialOperationsService.save(materialOperations);
			DirectReturnDetail.setMaterialId(material.getId());
			DirectReturnDetail.setDirectReturnId(DirectReturn.getId());
			DirectReturnDetail.setId(null);//清空前台可能产生的id，重新生成
			DirectReturnDetailService.save(DirectReturnDetail);
		}

		DirectReturnOperations operations = new DirectReturnOperations();
		operations.setReturnId(DirectReturn.getId());
		operations.setType(stock_type_sure);
		operations.setOperationId(DirectReturn.getId());// 新建入库的时候操作就是入库单id
		DirectReturnOperationsService.save(operations);

		// 计算重量体积
		// sql查询出合计数据
		MathStockNumber math = baseMapper.selectMathReturnNumberByReturnId(DirectReturn.getId());
		DirectReturn.setVolume(math.getVolumeSum());
		DirectReturn.setWeight(math.getWeightSum());
		DirectReturn.setTotal(math.getNumZ());
		DirectReturn.setScatteredNum(math.getScatteredNum());
		DirectReturn.setNumber(math.getNumber());
		DirectReturn.setIncomeId(getById(DirectReturn.getId()).getIncomeId());

		try {
			incomeService.directReturnIncomeMath(DirectReturn);
		} catch (RuntimeJsonMappingException e) {
			throw new RuntimeJsonMappingException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		baseMapper.updateById(DirectReturn);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "DirectReturns", allEntries = true)
	public void deleteDirectReturn(DirectReturn DirectReturn) {
		DirectReturn.setDelFlag(true);
		baseMapper.updateById(DirectReturn);
		//然后改异常单据的状态
		// 待选择
		Integer handle_abnormity_status_wait = CacheUtils.keyDict.get("handle_abnormity_status_wait").getValue();
		handleAbnormityService.changeStatusByIdAndStatus(DirectReturn.getHandleAbnormityId(), handle_abnormity_status_wait);
	}

	@Override
	@Cacheable("DirectReturns")
	public List<DirectReturn> selectAll() {
		QueryWrapper<DirectReturn> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void backDirectReturn(String DirectReturnId) {
		// 撤销销退
		Integer trunover_type_direct_return_back = CacheUtils.keyDict.get("trunover_type_direct_return_back").getValue();
		// 撤回单据就是来一波和确认单据相反得操作
		Integer stock_type_back = CacheUtils.keyDict.get("stock_type_back").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		DirectReturn DirectReturn = getById(DirectReturnId);
		// 不良品
		Integer material_type_rejects = CacheUtils.keyDict.get("material_type_rejects").getValue();

		List<DirectReturnDetail> details = DirectReturnDetailService.selectDirectReturnDetailByDirectReturnId(DirectReturnId);

		for (DirectReturnDetail DirectReturnDetail : details) {
			Material material = materialService.getMaterialByItemId(DirectReturnDetail.getItemId(),
					DirectReturnDetail.getBatch(), material_type_rejects);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() - DirectReturnDetail.getNumber());// 减去可用库存
				material.setDepotCode(material.getDepotCode() - DirectReturnDetail.getNumber());// 减去库存
				material.setWholeNum(material.getWholeNum() - DirectReturnDetail.getWholeNum());//减去整库存
				material.setScatteredNum(material.getScatteredNum() - DirectReturnDetail.getScatteredNum());//减去零库存
				materialService.updateById(material);
			}
			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), DirectReturnDetail.getDepot(),
					DirectReturnDetail.getNumber(), DirectReturnDetail.getWholeNum(),DirectReturnDetail.getScatteredNum(),false);
			if (StringUtils.checkValNotNull(DirectReturnDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), DirectReturnDetail.getTray(),
						DirectReturnDetail.getNumber(), false);
			}
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(DirectReturn.getSystemCode());
			materialOperations.setFromType(trunover_type_direct_return_back);// 销退撤销
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(DirectReturnDetail.getNumber());
			materialOperations.setWholeNum(DirectReturnDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(DirectReturnDetail.getScatteredNum());//增加零库存
			materialOperations.setType(2);// 销退撤销为-
			materialOperationsService.save(materialOperations);
		}
		// 记录人得操作
		DirectReturnOperations operations = new DirectReturnOperations();
		operations.setReturnId(DirectReturn.getId());
		operations.setType(stock_type_back);
		operations.setOperationId(DirectReturn.getId());// 撤销入库的时候操作就是入库单id
		DirectReturnOperationsService.save(operations);
		// 还有计算退库装卸费  撤销该费用
		if(incomeService.getById(DirectReturn)!=null){
			incomeService.deleteIncome(incomeService.getById(DirectReturn));//有输入就删除没有就不用管
		}
		DirectReturn.setIncomeId("无");//
		DirectReturn.setStatus(modify_status_await);
		baseMapper.updateById(DirectReturn);
	}

	@Override
	public void lockDirectReturn(String materialId) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		
		//找到详情里的物料ID
		Set<String> returnIds = DirectReturnDetailService.selectDirectReturnIdsByMaterialId(materialId);
		
		QueryWrapper<DirectReturn> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.in("id", returnIds);
		wrapper.eq("status", modify_status_revocation);
		DirectReturn s = new DirectReturn();
		s.setStatus(modify_status_lock);
		baseMapper.update(s, wrapper);
		
	}

}
