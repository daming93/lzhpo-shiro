package com.lzhpo.client.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.entity.DeliverContractMainDetail;
import com.lzhpo.client.mapper.DeliverContractMainDetailMapper;
import com.lzhpo.client.service.IDeliverContractMainDetailService;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-10-22
 */
@Service
public class DeliverContractMainDetailServiceImpl extends ServiceImpl<DeliverContractMainDetailMapper, DeliverContractMainDetail> implements IDeliverContractMainDetailService {
	@Override
    public long getDeliverContractMainDetailCount(String name) {
        QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMainDetails", allEntries = true)
    public DeliverContractMainDetail saveDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail) {
        baseMapper.insert(deliverContractMainDetail);
        /**
	*预留编辑代码 
	*/
        return deliverContractMainDetail;
    }

    @Override
    public DeliverContractMainDetail getDeliverContractMainDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMainDetails", allEntries = true)
    public void updateDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail) {
        baseMapper.updateById(deliverContractMainDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "DeliverContractMainDetails", allEntries = true)
    public void deleteDeliverContractMainDetail(DeliverContractMainDetail deliverContractMainDetail) {
        deliverContractMainDetail.setDelFlag(true);
        baseMapper.updateById(deliverContractMainDetail);
    }

    @Override
    @Cacheable("DeliverContractMainDetails")
    public List<DeliverContractMainDetail> selectAll() {
        QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void deleteAllDetailByMainId(String mainId) {
		QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId);
		baseMapper.delete(wrapper);
	}

	@Override
	public List<DeliverContractMainDetail> getListByMainId(String mainId) {
		QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId);
		return baseMapper.selectList(wrapper);
	}

	@Override
	public DeliverContractMainDetail selectDetailMoneyByInfoNoRange(String mainId, String proviceId, String cityId,
			String areaId) {
		// TODO Auto-generated method stub
		//先看区级 //包含关系都是含下不含上
		QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).eq("area_id", areaId);
		wrapper.orderByAsc("max_number - min_number");
		DeliverContractMainDetail detail =baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
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

	@Override
	public DeliverContractMainDetail selectDetailMoneyByInfo(String mainId, String proviceId, String cityId,
			String areaId, Object range,Integer type) {
		// TODO Auto-generated method stub
		//先看区级 //包含关系都是含下不含上
		QueryWrapper<DeliverContractMainDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).eq("area_id", areaId).ge("max_number", range).lt("min_number", range).eq("type", type);
		wrapper.orderByAsc("max_number - min_number");
		DeliverContractMainDetail detail =baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
		if(detail!=null){
			return detail;
		}else{
			//找市一级
			wrapper = new QueryWrapper<>();
			wrapper.eq("contract_id", mainId).eq("province_id", proviceId).eq("city_id", cityId).ge("max_number", range).lt("min_number", range).eq("type", type);
			wrapper.orderByAsc("max_number - min_number");
			detail = baseMapper.selectList(wrapper)!=null&&baseMapper.selectList(wrapper).size()>0?baseMapper.selectList(wrapper).get(0):null;
			if(detail!=null){
				return detail;
			}else{
				//找省一级
				wrapper = new QueryWrapper<>();
				wrapper.eq("contract_id", mainId).eq("province_id", proviceId).ge("max_number", range).lt("min_number", range).eq("type", type);
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
