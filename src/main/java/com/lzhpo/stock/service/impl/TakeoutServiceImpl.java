package com.lzhpo.stock.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.RuntimeJsonMappingException;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.entity.MathStockNumber;
import com.lzhpo.stock.entity.Takeout;
import com.lzhpo.stock.entity.TakeoutDetail;
import com.lzhpo.stock.entity.TakeoutOperations;
import com.lzhpo.stock.mapper.TakeoutMapper;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.stock.service.IStorageService;
import com.lzhpo.stock.service.ITakeoutDetailService;
import com.lzhpo.stock.service.ITakeoutOperationsService;
import com.lzhpo.stock.service.ITakeoutService;
import com.lzhpo.sys.service.IGenerateNoService;

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
		takeout.setNumber(null);
		takeout.setStatus(modify_status_await);
		takeout.setCode(generateNoService.nextCode("CK"));

		baseMapper.insert(takeout);

		Set<TakeoutDetail> detailSet = takeout.getDetailSet();

		for (TakeoutDetail takeoutDetail : detailSet) {
			// 根据品项找到物料对应的信息
			// Clientitem item =
			// itemSeivice.getClientitemById(takeoutDetail.getItemId());
			takeoutDetail.setTakeoutId(takeout.getId());
			takeoutDetail.setClientId(takeout.getClientId());
			takeoutDetail.setId(null);
			// 应该在这里把可用库存改成锁定库存 这里也是做库存得二次认证
			// 有materialId使用
			try {
				materialSerivice.lockMaterial(takeoutDetail.getMaterial(), takeoutDetail.getNumber());
			} catch (RuntimeJsonMappingException e) {
				throw new RuntimeJsonMappingException(e.getMessage());
			} catch (Exception e) {
				e.printStackTrace();
			}
			takeoutDetailService.save(takeoutDetail);
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(takeout.getCode());
			materialOperations.setFromType(trunover_type_takeout_new);// 出库库
			materialOperations.setMaterialId(takeoutDetail.getMaterial());
			materialOperations.setNumber(takeoutDetail.getNumber());
			materialOperations.setType(2);// 出库为-
			materialOperationsService.save(materialOperations);
			
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
		return baseMapper.selectById(id);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Takeouts", allEntries = true)
	public void updateTakeout(Takeout takeout) {
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

		TakeoutOperations operations = new TakeoutOperations();
		operations.setTakeoutId(takeout.getId());
		operations.setType(stock_type_sure);
		operations.setOperationId(takeout.getId());// 新建出库的时候操作就是出库单id
		operationService.save(operations);
		baseMapper.updateById(takeout);
		/**
		 * 预留编辑代码
		 */
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	@CacheEvict(value = "Takeouts", allEntries = true)
	public void deleteTakeout(Takeout takeoutWeb) {
		//撤销出库
		Integer trunover_type_takeout_back = CacheUtils.keyDict.get("trunover_type_takeout_back").getValue();
		Takeout takeout = getTakeoutById(takeoutWeb.getId());
		List<TakeoutDetail> detailSet = takeoutDetailService.selecttakeoutDetailBytakeoutId(takeoutWeb.getId());
		for (TakeoutDetail takeoutDetail : detailSet) {
			// 先加出库存
			materialSerivice.unlockMaterial(takeoutDetail.getMaterial(), takeoutDetail.getNumber());
			// 记录流水
			MaterialOperations materialOperations = new MaterialOperations();
			materialOperations.setFromCode(takeout.getCode());
			materialOperations.setFromType(trunover_type_takeout_back);// 出库
			materialOperations.setMaterialId(takeoutDetail.getMaterial());
			materialOperations.setNumber(takeoutDetail.getNumber());
			materialOperations.setType(1);// 出库撤销为+
			materialOperationsService.save(materialOperations);
		}
		takeout.setDelFlag(true);
		baseMapper.updateById(takeout);
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
		baseMapper.updateById(takeout);
	}

}
