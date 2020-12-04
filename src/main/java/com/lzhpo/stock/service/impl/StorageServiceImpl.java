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
import com.lzhpo.stock.entity.Storage;
import com.lzhpo.stock.entity.StorageDetail;
import com.lzhpo.stock.entity.StorageOperations;
import com.lzhpo.stock.mapper.StorageMapper;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IMaterialTrayService;
import com.lzhpo.stock.service.IStorageDetailService;
import com.lzhpo.stock.service.IStorageOperationsService;
import com.lzhpo.stock.service.IStorageService;
import com.lzhpo.sys.service.IGenerateNoService;

/**
 * <p>
 * 入库主表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Service
public class StorageServiceImpl extends ServiceImpl<StorageMapper, Storage> implements IStorageService {

	@Autowired
	private IStorageDetailService detailService;

	@Autowired
	private IGenerateNoService generateNoService;

	@Autowired
	private IStorageOperationsService storageOperationsService;

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
	public long getStorageCount(String name) {
		QueryWrapper<Storage> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Storages", allEntries = true)
	public Storage saveStorage(Storage storage) {
		Integer type_new = CacheUtils.keyDict.get("stock_type_new").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		storage.setNumber(null);// 确定的时候在核对数量
		storage.setStatus(modify_status_await);
		// 入库单号
		storage.setCode(generateNoService.nextCode("RK"));
		// 子表保存
		baseMapper.insert(storage);
		List<StorageDetail> detailSet = storage.getDetailSet();
		for (StorageDetail storageDetail : detailSet) {
			// 根据品项找到物料对应的信息
			// Clientitem item =
			// itemSeivice.getClientitemById(storageDetail.getItemId());
			storageDetail.setStorageId(storage.getId());
			detailService.save(storageDetail);
		}
		// 记录操作
		StorageOperations operations = new StorageOperations();
		operations.setStorageId(storage.getId());
		operations.setType(type_new);
		operations.setOperationId(storage.getId());// 新建入库的时候操作就是入库单id
		storageOperationsService.save(operations);
		return storage;
	}

	@Override
	public Storage getStorageById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Storages", allEntries = true)
	public void updateStorage(Storage storage) {
		//新建入库
		Integer trunover_type_storage_new = CacheUtils.keyDict.get("trunover_type_storage_new").getValue();
		
		Integer stock_type_sure = CacheUtils.keyDict.get("stock_type_sure").getValue();
		
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		
		//良品
		Integer material_type_good = CacheUtils.keyDict.get("material_type_good").getValue();
		
		//调账
		Integer transportation_type_adjustment =  CacheUtils.keyDict.get("transportation_type_adjustment").getValue();
		storage.setStatus(modify_status_revocation);
		// 删除有关子表
		detailService.deleteStorageDetailById(storage.getId());
		List<StorageDetail> detailSet = storage.getDetailSet();
		for (StorageDetail storageDetail : detailSet) {
			// 根据品项找到物料对应的信息
			// 先加入库存
			Material material = materialService.getMaterialByItemId(storageDetail.getItemId(),
					storageDetail.getBatch(),material_type_good);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() + storageDetail.getNumber());// 增加可用库存
				material.setDepotCode(material.getDepotCode() + storageDetail.getNumber());// 增加库存
				material.setWholeNum(material.getWholeNum() + storageDetail.getWholeNum());//增加整库存
				material.setScatteredNum(material.getScatteredNum() + storageDetail.getScatteredNum());//增加零库存
				materialService.updateById(material);
			} else {
				material = new Material();
				material.setDelFlag(false);
				material.setAvailableNum(storageDetail.getNumber());// 增加可用库存
				material.setDepotCode(storageDetail.getNumber());// 增加库存
				material.setWholeNum(storageDetail.getWholeNum());//增加整库存
				material.setScatteredNum(storageDetail.getScatteredNum());//增加零库存
				material.setItemId(storageDetail.getItemId());
				material.setBatchNumber(storageDetail.getBatch());
				material.setClientId(storage.getClientId());
				material.setLockCode(0);
				material.setType(material_type_good);//良品
				materialService.save(material);
			}

			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), storageDetail.getDepot(),
					storageDetail.getNumber(),storageDetail.getWholeNum(),storageDetail.getScatteredNum(), true);
			if (StringUtils.checkValNotNull(storageDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), storageDetail.getTray(),
						storageDetail.getNumber(), true);
			}
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(storage.getCode());
			materialOperations.setFromType(trunover_type_storage_new);// 入库
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(storageDetail.getNumber());
			materialOperations.setWholeNum(storageDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(storageDetail.getScatteredNum());//增加零库存
			
			materialOperations.setType(1);// 入库为＋
			materialOperationsService.save(materialOperations);
			storageDetail.setMaterialId(material.getId());
			storageDetail.setStorageId(storage.getId());
			storageDetail.setId(null);//清空前台编辑产生的id
			detailService.save(storageDetail);

		}
		// 记录人得操作

		StorageOperations operations = new StorageOperations();
		operations.setStorageId(storage.getId());
		operations.setType(stock_type_sure);
		operations.setOperationId(storage.getId());// 新建入库的时候操作就是入库单id
		storageOperationsService.save(operations);

		// sql查询出合计数据
		MathStockNumber math = baseMapper.selectMathStorageNumberByStorageId(storage.getId());
		storage.setVolume(math.getVolumeSum());
		storage.setWeight(math.getWeightSum());
		storage.setTotal(math.getNumZ());
		storage.setTrayNum(math.getTray());
		storage.setNumber(math.getNumber());
		storage.setScatteredNum(math.getScatteredNum());
		storage.setIncomeId(getById(storage.getId()).getIncomeId());
		// 还有计算入库装卸费 
		try {
			if(!transportation_type_adjustment.equals(storage.getAdjustment())){//不是调账就计算费用
				incomeService.storageIncomeMath(storage);
			}
		} catch (RuntimeJsonMappingException e) {
			throw new RuntimeJsonMappingException(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
		}
		baseMapper.updateById(storage);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	public void backStorage(String storageId) {
		//撤销入库
		Integer trunover_type_storage_back = CacheUtils.keyDict.get("trunover_type_storage_back").getValue();
		// 撤回单据就是来一波和确认单据相反得操作
		Integer stock_type_back = CacheUtils.keyDict.get("stock_type_back").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		Storage storage = getById(storageId);
		List<StorageDetail> detailSet = detailService.selectStorageDetailByStorageId(storageId);
		//良品
		Integer material_type_good = CacheUtils.keyDict.get("material_type_good").getValue();
		for (StorageDetail storageDetail : detailSet) {
			// 先减去库存
			Material material = materialService.getMaterialByItemId(storageDetail.getItemId(),
					storageDetail.getBatch(),material_type_good);
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() - storageDetail.getNumber());// 减去可用库存
				material.setDepotCode(material.getDepotCode() - storageDetail.getNumber());// 减去库存
				material.setWholeNum(material.getWholeNum() - storageDetail.getWholeNum());//减去整库存
				material.setScatteredNum(material.getScatteredNum() - storageDetail.getScatteredNum());//减去零库存
				materialService.updateById(material);
			}
			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), storageDetail.getDepot(),
					storageDetail.getNumber(), storageDetail.getWholeNum(),storageDetail.getScatteredNum(),false);
			if (StringUtils.checkValNotNull(storageDetail.getTray())) {
				materialTrayService.mathNumberBymaterialIdAndTrayId(material.getId(), storageDetail.getTray(),
						storageDetail.getNumber(), false);
			}
			
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(storage.getCode());
			materialOperations.setFromType(trunover_type_storage_back);// 入库
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(storageDetail.getNumber());
			materialOperations.setWholeNum(storageDetail.getWholeNum());//增加整库存
			materialOperations.setScatteredNum(storageDetail.getScatteredNum());//增加零库存
			materialOperations.setType(2);// 入库撤销为-
			materialOperationsService.save(materialOperations);
		}
		// 记录人得操作
		StorageOperations operations = new StorageOperations();
		operations.setStorageId(storage.getId());
		operations.setType(stock_type_back);
		operations.setOperationId(storage.getId());// 撤销入库的时候操作就是入库单id
		storageOperationsService.save(operations);
		// 还有计算入库装卸费 （待完成） 撤销该费用
		storage.setStatus(modify_status_await);
		baseMapper.updateById(storage);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Storages", allEntries = true)
	public void deleteStorage(Storage storage) {
		storage.setDelFlag(true);
		baseMapper.updateById(storage);
	}

	@Override
	@Cacheable("Storages")
	public List<Storage> selectAll() {
		QueryWrapper<Storage> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public MathStockNumber selectMathStorageNumberByStorageId(String storageId) {
		return baseMapper.selectMathStorageNumberByStorageId(storageId);
	}

	@Override
	public void lockStorage(String materialId) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		
		//找到详情里的物料ID
		Set<String> StorageIds = detailService.selectStorageIdsByMaterialId(materialId);
		
		QueryWrapper<Storage> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		wrapper.in("id", StorageIds);
		wrapper.eq("status", modify_status_revocation);
		
		Storage s = new Storage();
		s.setStatus(modify_status_lock);
		baseMapper.update(s, wrapper);
		
	}

}
