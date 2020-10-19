package com.lzhpo.deliver.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.deliver.entity.VehicleContractMainDetail;
import com.lzhpo.deliver.mapper.VehicleContractMainDetailMapper;
import com.lzhpo.deliver.service.IVehicleContractMainDetailService;
/**
 * <p>
 * 合同收费项 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-18
 */
@Service
public class VehicleContractMainDetailServiceImpl extends ServiceImpl<VehicleContractMainDetailMapper, VehicleContractMainDetail> implements IVehicleContractMainDetailService {
	@Override
    public long getVehicleContractMainDetailCount(String name) {
        QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMainDetails", allEntries = true)
    public VehicleContractMainDetail saveVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail) {
        baseMapper.insert(vehicleContractMainDetail);
        /**
	*预留编辑代码 
	*/
        return vehicleContractMainDetail;
    }

    @Override
    public VehicleContractMainDetail getVehicleContractMainDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMainDetails", allEntries = true)
    public void updateVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail) {
        baseMapper.updateById(vehicleContractMainDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "VehicleContractMainDetails", allEntries = true)
    public void deleteVehicleContractMainDetail(VehicleContractMainDetail vehicleContractMainDetail) {
        vehicleContractMainDetail.setDelFlag(true);
        baseMapper.updateById(vehicleContractMainDetail);
    }

    @Override
    @Cacheable("VehicleContractMainDetails")
    public List<VehicleContractMainDetail> selectAll() {
        QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void deleteAllDetailByMainId(String mainId) {
		QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId);
		baseMapper.delete(wrapper);
		
	}

	@Override
	public BigDecimal selectDetailMoneyByInfo(String mainId, String proviceId, String cityId, String areaId,Integer range) {
		// TODO Auto-generated method stub
		//先看区级 //包含关系都是含下不含上
		QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).eq("area_id", areaId).ge("min_number", range).lt("max_number", range);
		wrapper.orderByAsc("max_number - min_number");
		VehicleContractMainDetail detail =baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
		if(detail!=null){
			return detail.getMoney();
		}else{
			//找市一级
			wrapper = new QueryWrapper<>();
			wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).ge("min_number", range).lt("max_number", range);
			wrapper.orderByAsc("max_number - min_number");
			detail = baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
			if(detail!=null){
				return detail.getMoney();
			}else{
				//找省一级
				wrapper = new QueryWrapper<>();
				wrapper.eq("contract_id", mainId).eq("province_id", proviceId).ge("min_number", range).lt("max_number", range);
				wrapper.orderByAsc("max_number - min_number");
				detail = baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
				if(detail!=null){
					return detail.getMoney();
				}else{
					return null;//如果都没有找到就为空
				}
			}
		}
	}

	@Override
	public List<VehicleContractMainDetail> getListByMainId(String mainId) {
		QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public VehicleContractMainDetail selectDetailMoneyByInfoNoRange(String mainId, String proviceId, String cityId,
			String areaId) {
		// TODO Auto-generated method stub
		//先看区级 //包含关系都是含下不含上
		QueryWrapper<VehicleContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).eq("area_id", areaId);
		wrapper.orderByAsc("max_number - min_number");
		VehicleContractMainDetail detail =baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
		if(detail!=null){
			return detail;
		}else{
			//找市一级
			wrapper = new QueryWrapper<>();
			wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId);
			wrapper.orderByAsc("max_number - min_number");
			detail = baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
			if(detail!=null){
				return detail;
			}else{
				//找省一级
				wrapper = new QueryWrapper<>();
				wrapper.eq("contract_id", mainId).eq("province_id", proviceId);
				wrapper.orderByAsc("max_number - min_number");
				detail = baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
				if(detail!=null){
					return detail;
				}else{
					return null;//如果都没有找到就为空
				}
			}
		}
	}
}
