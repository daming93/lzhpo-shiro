package com.lzhpo.warehouse.service.impl;

import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.entity.MaterialDepot;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.service.IMaterialDepotService;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IStorageService;
import com.lzhpo.sys.service.IGenerateNoService;
import com.lzhpo.warehouse.entity.MaterialManage;
import com.lzhpo.warehouse.entity.MaterialManageDetail;
import com.lzhpo.warehouse.mapper.MaterialManageMapper;
import com.lzhpo.warehouse.service.IMaterialManageDetailService;
import com.lzhpo.warehouse.service.IMaterialManageService;

import cn.hutool.core.lang.UUID;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
@Service
public class MaterialManageServiceImpl extends ServiceImpl<MaterialManageMapper, MaterialManage>
		implements IMaterialManageService {
	@Autowired
	private IGenerateNoService generateNoService;
	@Autowired
	private IMaterialService materialSerivice;
	@Autowired
	private IMaterialManageDetailService materialManageDetailService;
	@Autowired
	private IMaterialOperationsService materialOperationsService;
	@Autowired
	private IStorageService storageService;
	@Autowired
	private IMaterialDepotService materialDepotService;

	@Override
	public long getMaterialManageCount(String name) {
		QueryWrapper<MaterialManage> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
	@CacheEvict(value = "MaterialManages", allEntries = true)
	public MaterialManage saveMaterialManage(MaterialManage materialManage) {
		// 转良 物料流水记录
		Integer trunover_type_manage_good = CacheUtils.keyDict.get("trunover_type_manage_good").getValue();
		// 转不良 物料流水记录
		Integer trunover_type_manage_bad = CacheUtils.keyDict.get("trunover_type_manage_bad").getValue();

		// 转良 管理类型
		// Integer material_regulate_turn_good =
		// CacheUtils.keyDict.get("material_regulate_turn_good").getValue();
		// 转不良 管理类型
		// Integer material_regulate_turn_bad =
		// CacheUtils.keyDict.get("material_regulate_turn_bad").getValue();

		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();

		materialManage.setModifyStatus(modify_status_await);

		switch (materialManage.getStatus()) {
		case 1: // 转良 管理类型
			materialManage.setSystemCode(generateNoService.nextCode("ZL"));
			break;

		default: // 转不良 管理类型
			materialManage.setSystemCode(generateNoService.nextCode("ZBL"));
			break;
		}
		baseMapper.insert(materialManage);
		// 现在有manage得id了
		Set<MaterialManageDetail> detailSet = materialManage.getDetailSet();
		// 明细要怎么作
		for (MaterialManageDetail materialManageDetail : detailSet) {
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(materialManage.getSystemCode());
			switch (materialManage.getStatus()) {
			case 1: // 转良 物料流水记录
				materialOperations.setFromType(trunover_type_manage_good);
				break;

			default: // 转不良 物料流水记录
				materialOperations.setFromType(trunover_type_manage_bad);
				break;
			}
			materialOperations.setMaterialId(materialManageDetail.getMaterial());
			materialOperations.setNumber(materialManageDetail.getNumber());
			materialOperations.setType(2);// 出库为-
			materialOperationsService.save(materialOperations);
			// 锁入库单
			storageService.lockStorage(materialManageDetail.getMaterial());
			materialManageDetail.setManageId(materialManage.getId());
			// 在带确认得状态下只减不加，当确认之后 良品数量和不良品数量在开始增减
			try {
				List<MaterialDepot> mdepotList = materialSerivice.lockMaterial(materialManageDetail.getMaterial(),
						materialManageDetail.getNumber(), null);
				for (MaterialDepot materialDepot : mdepotList) {
					materialManageDetail.setDepot(materialDepot.getDepotId());
					materialManageDetail.setNumber(materialDepot.getNumber());
					materialManageDetail.setId(UUID.randomUUID().toString());
					materialManageDetailService.save(materialManageDetail);
				}
			} catch (RuntimeJsonMappingException e) {
				throw new RuntimeJsonMappingException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return materialManage;
	}

	@Override
	public MaterialManage getMaterialManageById(String id) {
		return baseMapper.selectById(id);

	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialManages", allEntries = true)
	public void updateMaterialManage(MaterialManage materialManage) {
		// 良品
		Integer material_type_good = CacheUtils.keyDict.get("material_type_good").getValue();
		// 不良品
		Integer material_type_rejects = CacheUtils.keyDict.get("material_type_rejects").getValue();
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		materialManage.setModifyStatus(modify_status_lock);
		Set<MaterialManageDetail> detailSet = materialManage.getDetailSet();
		// 明细
		for (MaterialManageDetail materialManageDetail : detailSet) {
			// 这个和托盘储位就有关系了 就是标准得
			Material material = null;
			// 要增加良品和不良品 得物料
			switch (materialManage.getStatus()) {
			case 1: // 转良
				material = materialSerivice.getMaterialByItemId(materialManageDetail.getItemId(),
						materialManageDetail.getBatch(), material_type_good);
				break;

			default: // 转不良
				material = materialSerivice.getMaterialByItemId(materialManageDetail.getItemId(),
						materialManageDetail.getBatch(), material_type_rejects);
				break;
			}
			// 先找到有没有对应物料
			if (material != null) {
				material.setDelFlag(false);
				material.setAvailableNum(material.getAvailableNum() + materialManageDetail.getNumber());// 增加可用库存
				material.setDepotCode(material.getDepotCode() + materialManageDetail.getNumber());// 增加库存
				materialSerivice.updateById(material);
			} else {
				material = new Material();
				material.setDelFlag(false);
				material.setAvailableNum(materialManageDetail.getNumber());// 增加可用库存
				material.setDepotCode(materialManageDetail.getNumber());// 增加库存
				material.setItemId(materialManageDetail.getItemId());
				material.setBatchNumber(materialManageDetail.getBatch());
				material.setClientId(materialManage.getClientId());
				material.setLockCode(0);
				switch (materialManage.getStatus()) {
				case 1: // 转良
					material.setType(material_type_good);// 良品
					break;

				default: // 转不良
					material.setType(material_type_rejects);// 良品
					break;
				}

				materialSerivice.save(material);
			}
			// 记录入库数据
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(materialManage.getSystemCode());
			// 转良 物料流水记录
			Integer trunover_type_manage_good = CacheUtils.keyDict.get("trunover_type_manage_good").getValue();
			// 转不良 物料流水记录
			Integer trunover_type_manage_bad = CacheUtils.keyDict.get("trunover_type_manage_bad").getValue();
			
			switch (materialManage.getStatus()) {
			case 1: // 转良
				materialOperations.setFromType(trunover_type_manage_good);// 转良 物料流水记录
				break;

			default: // 转不良
				materialOperations.setFromType(trunover_type_manage_bad);// 转不良 物料流水记录
				break;
			}
			materialOperations.setMaterialId(material.getId());
			materialOperations.setNumber(materialManageDetail.getNumber());
			materialOperations.setType(1);// 入库为＋
			materialOperationsService.save(materialOperations);

			// 托盘储位分配数量
			materialDepotService.mathNumberBymaterialIdAndDepotId(material.getId(), materialManageDetail.getDepot(),
					materialManageDetail.getNumber(), true);
		}

		baseMapper.updateById(materialManage);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "MaterialManages", allEntries = true)
	public void deleteMaterialManage(MaterialManage materialManage) {
		materialManage.setDelFlag(true);
		baseMapper.updateById(materialManage);
	}

	@Override
	@Cacheable("MaterialManages")
	public List<MaterialManage> selectAll() {
		QueryWrapper<MaterialManage> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

}
