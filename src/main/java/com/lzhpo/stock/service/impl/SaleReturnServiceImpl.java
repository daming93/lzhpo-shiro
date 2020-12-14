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
import com.lzhpo.stock.entity.SaleReturn;
import com.lzhpo.stock.entity.SaleReturnDetail;
import com.lzhpo.stock.entity.SaleReturnOperations;
import com.lzhpo.stock.mapper.SaleReturnMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IMaterialTrayService;
import com.lzhpo.stock.service.ISaleReturnDetailService;
import com.lzhpo.stock.service.ISaleReturnOperationsService;
import com.lzhpo.stock.service.ISaleReturnService;
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
public class SaleReturnServiceImpl extends ServiceImpl<SaleReturnMapper, SaleReturn> implements ISaleReturnService {

	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private ISaleReturnDetailService saleReturnDetailService;
	@Autowired
	private ISaleReturnOperationsService saleReturnOperationsService;
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

	@Override
	public long getSaleReturnCount(String name) {
		QueryWrapper<SaleReturn> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturns", allEntries = true)
	public SaleReturn saveSaleReturn(SaleReturn saleReturn) {
		Integer type_new = CacheUtils.keyDict.get("stock_type_new").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		saleReturn.setNumber(null);// 确定的时候在核对数量
		saleReturn.setStatus(modify_status_await);
		// 入库单号
		saleReturn.setSystemCode(generateNoService.nextCode("XT"));
		baseMapper.insert(saleReturn);
		List<SaleReturnDetail> detail = saleReturn.getDetailSet();
		for (SaleReturnDetail saleReturnDetail : detail) {
			saleReturnDetail.setSalesReturnId(saleReturn.getId());
			saleReturnDetailService.save(saleReturnDetail);
		}
		// 记录操作
		SaleReturnOperations operations = new SaleReturnOperations();
		operations.setReturnId(saleReturn.getId());
		operations.setType(type_new);
		operations.setOperationId(saleReturn.getId());// 新建入库的时候操作就是入库单id
		saleReturnOperationsService.save(operations);
		return saleReturn;
	}

	@Override
	public SaleReturn getSaleReturnById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturns", allEntries = true)
	public void updateSaleReturn(SaleReturn saleReturn) {
		// 新建销退
		Integer trunover_type_return_new = CacheUtils.keyDict.get("trunover_type_return_new").getValue();

		Integer stock_type_sure = CacheUtils.keyDict.get("stock_type_sure").getValue();

		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();

		// 不良品
		Integer material_type_rejects = CacheUtils.keyDict.get("material_type_rejects").getValue();

		saleReturn.setStatus(modify_status_revocation);
		// 删除有关子表
		saleReturnDetailService.deleteSaleReturnDetailByReturnId(saleReturn.getId());
		List<SaleReturnDetail> detail = saleReturn.getDetailSet();
		for (SaleReturnDetail saleReturnDetail : detail) {
			// 根据品项找到物料对应的信息
			// 先加入库存
			Material material = materialService.getMaterialByItemId(saleReturnDetail.getItemId(),
					saleReturnDetail.getBatch(), material_type_rejects);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() + saleReturnDetail.getNumber());// 增加可用库存
				material.setDepotCode(material.getDepotCode() + saleReturnDetail.getNumber());// 增加库存
				material.setWholeNum(material.getWholeNum() + saleReturnDetail.getWholeNum());//增加整库存
				material.setScatteredNum(material.getScatteredNum() + saleReturnDetail.getScatteredNum());//增加零库存
				materialService.updateById(material);
			} else {
				material = new Material();
				material.setDelFlag(false);
				material.setAvailableNum(saleReturnDetail.getNumber());// 增加可用库存
				material.setDepotCode(saleReturnDetail.getNumber());// 增加库存
				material.setWholeNum(saleReturnDetail.getWholeNum());//增加整库存
				material.setScatteredNum(saleReturnDetail.getScatteredNum());//增加零库存
				material.setItemId(saleReturnDetail.getItemId());
				material.setBatchNumber(saleReturnDetail.getBatch());
				material.setClientId(saleReturn.getClientId());
				material.setLockCode(0);
				material.setType(material_type_rejects);// 不良品
				materialService.save(material);
			}

			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), saleReturnDetail.getDepot(),
					saleReturnDetail.getNumber(),saleReturnDetail.getWholeNum(),saleReturnDetail.getScatteredNum(), true);
			if (StringUtils.checkValNotNull(saleReturnDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), saleReturnDetail.getTray(),
						saleReturnDetail.getNumber(), true);
			}
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(saleReturn.getSystemCode());
			materialOperations.setFromType(trunover_type_return_new);// 入库
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(saleReturnDetail.getNumber());
			materialOperations.setWholeNum(saleReturnDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(saleReturnDetail.getScatteredNum());//增加零库存
			materialOperations.setType(1);// 入库为＋
			materialOperationsService.save(materialOperations);
			saleReturnDetail.setMaterialId(material.getId());
			saleReturnDetail.setSalesReturnId(saleReturn.getId());
			saleReturnDetail.setId(null);//清空前台可能产生的id，重新生成
			saleReturnDetailService.save(saleReturnDetail);
		}

		SaleReturnOperations operations = new SaleReturnOperations();
		operations.setReturnId(saleReturn.getId());
		operations.setType(stock_type_sure);
		operations.setOperationId(saleReturn.getId());// 新建入库的时候操作就是入库单id
		saleReturnOperationsService.save(operations);

		// 计算重量体积
		// sql查询出合计数据
		MathStockNumber math = baseMapper.selectMathReturnNumberByReturnId(saleReturn.getId());
		saleReturn.setVolume(math.getVolumeSum());
		saleReturn.setWeight(math.getWeightSum());
		saleReturn.setTotal(math.getNumZ());
		saleReturn.setScatteredNum(math.getScatteredNum());
		saleReturn.setNumber(math.getNumber());
		saleReturn.setIncomeId(getById(saleReturn.getId()).getIncomeId());

		try {
			incomeService.saleReturnIncomeMath(saleReturn);
		} catch (RuntimeJsonMappingException e) {
			throw new RuntimeJsonMappingException(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		baseMapper.updateById(saleReturn);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "SaleReturns", allEntries = true)
	public void deleteSaleReturn(SaleReturn saleReturn) {
		saleReturn.setDelFlag(true);
		baseMapper.updateById(saleReturn);
	}

	@Override
	@Cacheable("SaleReturns")
	public List<SaleReturn> selectAll() {
		QueryWrapper<SaleReturn> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void backSaleReturn(String saleReturnId) {
		// 撤销销退
		Integer trunover_type_return_back = CacheUtils.keyDict.get("trunover_type_return_back").getValue();
		// 撤回单据就是来一波和确认单据相反得操作
		Integer stock_type_back = CacheUtils.keyDict.get("stock_type_back").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		SaleReturn saleReturn = getById(saleReturnId);
		// 不良品
		Integer material_type_rejects = CacheUtils.keyDict.get("material_type_rejects").getValue();

		List<SaleReturnDetail> details = saleReturnDetailService.selectSaleReturnDetailBySaleReturnId(saleReturnId);

		for (SaleReturnDetail saleReturnDetail : details) {
			Material material = materialService.getMaterialByItemId(saleReturnDetail.getItemId(),
					saleReturnDetail.getBatch(), material_type_rejects);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() - saleReturnDetail.getNumber());// 减去可用库存
				material.setDepotCode(material.getDepotCode() - saleReturnDetail.getNumber());// 减去库存
				material.setWholeNum(material.getWholeNum() - saleReturnDetail.getWholeNum());//减去整库存
				material.setScatteredNum(material.getScatteredNum() - saleReturnDetail.getScatteredNum());//减去零库存
				materialService.updateById(material);
			}
			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), saleReturnDetail.getDepot(),
					saleReturnDetail.getNumber(), saleReturnDetail.getWholeNum(),saleReturnDetail.getScatteredNum(),false);
			if (StringUtils.checkValNotNull(saleReturnDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), saleReturnDetail.getTray(),
						saleReturnDetail.getNumber(), false);
			}
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(saleReturn.getSystemCode());
			materialOperations.setFromType(trunover_type_return_back);// 销退撤销
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(saleReturnDetail.getNumber());
			materialOperations.setWholeNum(saleReturnDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(saleReturnDetail.getScatteredNum());//增加零库存
			materialOperations.setType(2);// 销退撤销为-
			materialOperationsService.save(materialOperations);
		}
		// 记录人得操作
		SaleReturnOperations operations = new SaleReturnOperations();
		operations.setReturnId(saleReturn.getId());
		operations.setType(stock_type_back);
		operations.setOperationId(saleReturn.getId());// 撤销入库的时候操作就是入库单id
		saleReturnOperationsService.save(operations);
		// 还有计算退库装卸费  撤销该费用
		if(incomeService.getById(saleReturn)!=null){
			incomeService.deleteIncome(incomeService.getById(saleReturn));//有输入就删除没有就不用管
		}
		saleReturn.setIncomeId("无");//
		saleReturn.setStatus(modify_status_await);
		baseMapper.updateById(saleReturn);
	}

	@Override
	public void lockSaleReturn(String materialId) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		
		//找到详情里的物料ID
		Set<String> returnIds = saleReturnDetailService.selectSaleReturnIdsByMaterialId(materialId);
		
		QueryWrapper<SaleReturn> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.in("id", returnIds);
		wrapper.eq("status", modify_status_revocation);
		SaleReturn s = new SaleReturn();
		s.setStatus(modify_status_lock);
		baseMapper.update(s, wrapper);
		
	}

}
