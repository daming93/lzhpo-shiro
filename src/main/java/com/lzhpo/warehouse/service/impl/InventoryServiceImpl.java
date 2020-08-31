package com.lzhpo.warehouse.service.impl;

import java.time.LocalDate;
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
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.Material;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.warehouse.entity.Inventory;
import com.lzhpo.warehouse.entity.InventoryMaterial;
import com.lzhpo.warehouse.mapper.InventoryMapper;
import com.lzhpo.warehouse.service.IInventoryMaterialService;
import com.lzhpo.warehouse.service.IInventoryService;

/**
 * <p>
 * 盘点表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-29
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

	@Autowired
	private IMaterialService materialService;

	@Autowired
	private IClientitemService clientitemService;

	@Autowired
	private IInventoryMaterialService inventoryMaterialService;

	@Override
	public long getInventoryCount(String name) {
		QueryWrapper<Inventory> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("del_flag", false);
		// wrapper.eq("name",name);
		return baseMapper.selectCount(wrapper);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Inventorys", allEntries = true)
	public Inventory saveInventory(Inventory inventory) {
		// 全盘
		Integer auditor_type_all = CacheUtils.keyDict.get("auditor_type_all").getValue();
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();

		// 待定 check_type_undetermined
		Integer check_type_undetermined = CacheUtils.keyDict.get("check_type_undetermined").getValue();

		// 异动
		// Integer auditor_type_transaction =
		// CacheUtils.keyDict.get("auditor_type_transaction").getValue();
		inventory.setInventoryTime(LocalDate.now());
		inventory.setInventoryStatus(modify_status_await);
		inventory.setStatus(check_type_undetermined);
		baseMapper.insert(inventory);
		// 这里要根据类型生成不同的盘点表 全盘 和 部分盘点 和 物料状态和批次有关
		List<Material> list = null;
		// 1 全盘
		if (auditor_type_all.equals(inventory.getAuditorType())) {
			Map<String, Object> mapRes = materialService.selectMaterial(null, null, null, 0, 999999,
					inventory.getBatchStatus(), null, inventory.getClientId(),null,null);
			list = (List<Material>) mapRes.get("list");

		} else {// 异动盘点
			list = materialService.selectListByClientAndTiem(inventory.getStartTime(), inventory.getEndTime(),
					inventory.getClientId());
		}
		for (Material material : list) {
			InventoryMaterial im = new InventoryMaterial();
			im.setInventoryId(inventory.getId());
			im.setMaterialId(material.getId()); // 这个物料id其实就包含了 品项 批次 良品 不良品 之类的
			im.setBatchNumber(material.getBatchNumber().toString());
			im.setType(material.getType());
			im.setDepotNum(Long.valueOf(material.getAvailableNum()));// 系统库存
			im.setWholeNum(material.getWholeNum());
			im.setScatteredNum(material.getScatteredNum());
			// 但是盘点的是当时的记录 应该记录下当时的品项名称 如果之后名称改了 不影响此间记录
			Clientitem item = clientitemService.getById(material.getItemId());
			im.setCode(item.getCode());
			im.setSuppliesSku(item.getName() + item.getSku());
			im.setUnitRate(item.getUnitRate());
			inventoryMaterialService.save(im);
		}
		return inventory;
	}

	@Override
	public Inventory getInventoryById(String id) {
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Inventorys", allEntries = true)
	public void updateInventory(Inventory inventory) {
		baseMapper.updateById(inventory);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Inventorys", allEntries = true)
	public void deleteInventory(Inventory inventory) {
		inventory.setDelFlag(true);
		baseMapper.updateById(inventory);
	}

	@Override
	@Cacheable("Inventorys")
	public List<Inventory> selectAll() {
		QueryWrapper<Inventory> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag", false);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public void ensureInventory(Inventory inventory) {
		// 锁定
		Integer modify_status_lock = CacheUtils.keyDict.get("modify_status_lock").getValue();
		// 正常 check_type_normal
		Integer check_type_normal = CacheUtils.keyDict.get("check_type_normal").getValue();

		// 差异 check_type_error
		Integer check_type_error = CacheUtils.keyDict.get("check_type_error").getValue();
		inventory.setInventoryStatus(modify_status_lock);
		// 这里看是否有差异
		// 就是找是不是子表里面没有 不等于0 或者 null的 different 的数值 然后把为null 的 人为盘点数 和 差异数量填进去
		long count = inventoryMaterialService.selectDetailDiffenent(inventory.getId());
		if (count != 0) {// 有差异
			inventory.setStatus(check_type_error);
		} else {
			inventory.setStatus(check_type_normal);
		}
		baseMapper.updateById(inventory);
	}
}
