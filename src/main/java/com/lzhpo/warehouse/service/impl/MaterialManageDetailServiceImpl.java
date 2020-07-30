package com.lzhpo.warehouse.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.material.item.entity.Clientitem;
import com.lzhpo.material.item.service.IClientitemService;
import com.lzhpo.stock.entity.MaterialOperations;
import com.lzhpo.stock.entity.TakeoutDetail;
import com.lzhpo.stock.service.IMaterialOperationsService;
import com.lzhpo.stock.service.IMaterialService;
import com.lzhpo.warehouse.entity.MaterialManageDetail;
import com.lzhpo.warehouse.mapper.MaterialManageDetailMapper;
import com.lzhpo.warehouse.service.IMaterialManageDetailService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-05
 */
@Service
public class MaterialManageDetailServiceImpl extends ServiceImpl<MaterialManageDetailMapper, MaterialManageDetail> implements IMaterialManageDetailService {
	
	@Autowired
	private IMaterialService materialSerivice;

	@Autowired
	private IClientitemService clientitemService;


	@Autowired
	private IMaterialOperationsService materialOperationsService;
	@Override
    public long getMaterialManageDetailCount(String name) {
        QueryWrapper<MaterialManageDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialManageDetails", allEntries = true)
    public MaterialManageDetail saveMaterialManageDetail(MaterialManageDetail materialManageDetail) {
        baseMapper.insert(materialManageDetail);
        /**
	*预留编辑代码 
	*/
        return materialManageDetail;
    }

    @Override
    public MaterialManageDetail getMaterialManageDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialManageDetails", allEntries = true)
    public void updateMaterialManageDetail(MaterialManageDetail materialManageDetail) {
        baseMapper.updateById(materialManageDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "MaterialManageDetails", allEntries = true)
    public void deleteMaterialManageDetail(MaterialManageDetail materialManageDetail, String code,Integer type) {
		//转良撤销
		Integer trunover_type_manage_good_back = CacheUtils.keyDict.get("trunover_type_manage_good_back").getValue();
		//转不良撤销
		Integer trunover_type_manage_bad_back = CacheUtils.keyDict.get("trunover_type_manage_bad_back").getValue();
		// 删除需要返回库存
		// 防止前台传来得数量是修改过得
		MaterialManageDetail entity = baseMapper.selectById(materialManageDetail.getId());
		// 解锁
		materialSerivice.unlockMaterial(entity.getMaterial(), entity.getWholeNum(),entity.getScatteredNum(),clientitemService.getById(entity.getItemId()).getUnitRate());
		// 记录流水
		MaterialOperations materialOperations = new MaterialOperations();
		materialOperations.setFromCode(code);
		switch (type) {
		case 1://转良
			materialOperations.setFromType(trunover_type_manage_good_back);// 转良
			break;

		default:
			materialOperations.setFromType(trunover_type_manage_bad_back);// 转不良
			break;
		}
		materialOperations.setMaterialId(materialManageDetail.getMaterial());
		materialOperations.setNumber(entity.getNumber());
		materialOperations.setWholeNum(entity.getWholeNum());
		materialOperations.setScatteredNum(entity.getScatteredNum());
		materialOperations.setType(2);// 撤销出库为+
		materialOperationsService.save(materialOperations);
		materialManageDetail.setDelFlag(true);
		baseMapper.deleteById(materialManageDetail.getId());//就直接删除
	}

    @Override
    @Cacheable("MaterialManageDetails")
    public List<MaterialManageDetail> selectAll() {
        QueryWrapper<MaterialManageDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void updateNumber(MaterialManageDetail detail, String code,Integer type) throws Exception {
		//转良编辑
		Integer trunover_type_manage_good_edit = CacheUtils.keyDict.get("trunover_type_manage_good_edit").getValue();
		//转不良编辑
		Integer trunover_type_manage_bad_edit = CacheUtils.keyDict.get("trunover_type_manage_bad_edit").getValue();
		// 防止前台传来得数量是修改过得
		MaterialManageDetail entity = baseMapper.selectById(detail.getId());
		Clientitem item = clientitemService.getById(entity.getItemId());
		if (detail.getWholeNum() != null&&detail.getScatteredNum()!=null) {
			detail.setNumber(detail.getWholeNum()* item.getUnitRate()+detail.getScatteredNum());
		}
		// 然后在锁住修改过的数量 传的数字就是变更的数字
		materialSerivice.lockMaterial(detail.getMaterial(), detail.getNumber() - entity.getNumber(),
				detail.getWholeNum() - entity.getWholeNum(),detail.getScatteredNum() - entity.getScatteredNum(),entity.getDepot());
		// 记录流水
		MaterialOperations materialOperations = new MaterialOperations();
		materialOperations.setFromCode(code);
		switch (type) {
		case 1://转良
			materialOperations.setFromType(trunover_type_manage_good_edit);// 转良编辑
			break;

		default:
			materialOperations.setFromType(trunover_type_manage_bad_edit);// 转不良编辑
			break;
		}
		materialOperations.setWholeNum(detail.getWholeNum()-entity.getWholeNum());
		materialOperations.setScatteredNum(detail.getScatteredNum()-entity.getScatteredNum());
		materialOperations.setMaterialId(detail.getMaterial());
		materialOperations.setNumber(detail.getNumber() - entity.getNumber());
		materialOperations.setType(2);// 出库为-
		materialOperationsService.save(materialOperations);

		baseMapper.updateById(detail);
	}


}
