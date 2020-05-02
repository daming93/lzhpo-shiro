package com.lzhpo.warehouse.service.impl;

import com.lzhpo.warehouse.entity.Depot;
import com.lzhpo.warehouse.mapper.DepotMapper;
import com.lzhpo.warehouse.service.IDepotService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 储位表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-23
 */
@Service
public class DepotServiceImpl extends ServiceImpl<DepotMapper, Depot> implements IDepotService {
	@Override
    public long getDepotCount(Depot depot) {
		String code = judCode(depot);
        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("code",code);
        if(depot.getId()!=null){
        	 wrapper.ne("id",depot.getId());
        }
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public Depot saveDepot(Depot depot) {
    	depot.setCode(judCode(depot));
        baseMapper.insert(depot);
        /**
	*预留编辑代码 
	*/
        return depot;
    }

    @Override
    public Depot getDepotById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public void updateDepot(Depot depot) {
    	depot.setCode(judCode(depot));
        baseMapper.updateById(depot);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Depots", allEntries = true)
    public void deleteDepot(Depot depot) {
    	//储位上有东西的时候不能更改3
        depot.setDelFlag(true);
        baseMapper.updateById(depot);
    }

    @Override
    @Cacheable("Depots")
    public List<Depot> selectAll() {
        QueryWrapper<Depot> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public String judCode(Depot depot) {
		StringBuffer buf = new StringBuffer();
		boolean flagA = depot.getSubsectionA()!=null;
		boolean flagB = flagA&&StringUtils.isNotBlank(depot.getSubsectionB());
		boolean flagC = flagB&&StringUtils.isNotBlank(depot.getSubsectionC());
		boolean flagD = flagC&&StringUtils.isNotBlank(depot.getSubsectionD());
		boolean flagE = flagD&&StringUtils.isNotBlank(depot.getSubsectionE());
		boolean flagF = flagE&&StringUtils.isNotBlank(depot.getSubsectionF());
		boolean flagG = flagF&&StringUtils.isNotBlank(depot.getSubsectionG());
		if(flagA){
			buf.append(depot.getSubsectionA());
		}else{
			return null;
		}
		if(flagB){
			buf.append(depot.getSubsectionB());
		}
		if(flagC){
			buf.append(depot.getSubsectionC());
		}
		if(flagD){
			buf.append(depot.getSubsectionD());
		}
		if(flagE){
			buf.append(depot.getSubsectionE());
		}
		if(flagF){
			buf.append(depot.getSubsectionF());
		}
		if(flagG){
			buf.append(depot.getSubsectionG());
		}
		return buf.toString();
	}


}
