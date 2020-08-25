package com.lzhpo.deliver.service.impl;

import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.common.util.CommomUtil;
import com.lzhpo.deliver.entity.ExpressBill;
import com.lzhpo.deliver.mapper.ExpressBillMapper;
import com.lzhpo.deliver.service.IExpressBillService;
import com.lzhpo.sys.service.IGenerateNoService;
import com.lzhpo.sys.service.ITerritoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 配送零单配送 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-08-12
 */
@Service
public class ExpressBillServiceImpl extends ServiceImpl<ExpressBillMapper, ExpressBill> implements IExpressBillService {
	@Autowired
	private ITerritoryService territoryService;
	
	@Autowired
	private IGenerateNoService generateNoService;
	@Override
    public long getExpressBillCount(String code) {
        QueryWrapper<ExpressBill> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("code",code);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ExpressBills", allEntries = true)
    public ExpressBill saveExpressBill(ExpressBill expressBill) {
    	// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		expressBill.setStatus(modify_status_await);
    	expressBill.setCode(generateNoService.nextCode("KSCD"));
        baseMapper.insert(expressBill);
        /**
	*预留编辑代码 
	*/
        return expressBill;
    }

    @Override
    public ExpressBill getExpressBillById(String id) {
    	ExpressBill entity = baseMapper.selectById(id);
		if (StringUtils.isNotBlank(entity.getSendProvinceId())) {
			entity.setProvinceName(territoryService.getById(entity.getSendProvinceId()).getName());
		}
		if (StringUtils.isNotBlank(entity.getSendCityId())) {
			entity.setCityName(territoryService.getById(entity.getSendCityId()).getName());
		}
		if (StringUtils.isNotBlank(entity.getSendAreaId())) {
			entity.setCountiesName(territoryService.getById(entity.getSendAreaId()).getName());
		}
		if (StringUtils.isNotBlank(entity.getReceiveProvinceId())) {
			entity.setReceiveProvinceName(territoryService.getById(entity.getReceiveProvinceId()).getName());
		}
		if (StringUtils.isNotBlank(entity.getReceiveCityId())) {
			entity.setReceiveCityName(territoryService.getById(entity.getReceiveCityId()).getName());
		}
		if (StringUtils.isNotBlank(entity.getReceiveAreaId())) {
			entity.setReceiveCountiesName(territoryService.getById(entity.getReceiveAreaId()).getName());
		}
		if (entity.getStatus() != null) {
			entity.setStatusStr(CommomUtil.valueToNameInDict(entity.getStatus(), "modify_status"));
		}
		entity.setSendDetail(entity.getProvinceName()+entity.getCityName()+entity.getCountiesName());
		entity.setReceiveDetail(entity.getReceiveProvinceName()+entity.getReceiveCityName()+entity.getReceiveCountiesName());
        return entity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ExpressBills", allEntries = true)
    public void updateExpressBill(ExpressBill expressBill) {
		// 可撤销
		Integer modify_status_revocation = CacheUtils.keyDict.get("modify_status_revocation").getValue();
		expressBill.setStatus(modify_status_revocation);
        baseMapper.updateById(expressBill);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "ExpressBills", allEntries = true)
    public void deleteExpressBill(ExpressBill expressBill) {
        expressBill.setDelFlag(true);
        baseMapper.updateById(expressBill);
    }

    @Override
    @Cacheable("ExpressBills")
    public List<ExpressBill> selectAll() {
        QueryWrapper<ExpressBill> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public ExpressBill getSendPeopelInfoBySendPhone(String sendPhone) {
    	QueryWrapper<ExpressBill> wrapper = new QueryWrapper<>();
        wrapper.eq("send_phone",sendPhone);
    	return baseMapper.selectOne(wrapper);
    }
    
    @Override
    public ExpressBill getReceivePeopelInfoByReceivePhone(String receivePhone) {
    	QueryWrapper<ExpressBill> wrapper = new QueryWrapper<>();
        wrapper.eq("receive_phone",receivePhone);
    	return baseMapper.selectOne(wrapper);
    }

	@Override
	public ExpressBill backExpressBill(String id) {
		// 待确认
		Integer modify_status_await = CacheUtils.keyDict.get("modify_status_await").getValue();
		ExpressBill entity = getById(id);
		entity.setStatus(modify_status_await);
		baseMapper.updateById(entity);
		return entity;
	}
    
}
