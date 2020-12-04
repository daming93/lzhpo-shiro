package com.lzhpo.stock.service.impl;

import com.lzhpo.common.config.MySysUser;
import com.lzhpo.common.init.CacheUtils;
import com.lzhpo.stock.entity.HandleAbnormity;
import com.lzhpo.stock.mapper.HandleAbnormityMapper;
import com.lzhpo.stock.service.IHandleAbnormityService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 异常处理表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-12-02
 */
@Service
public class HandleAbnormityServiceImpl extends ServiceImpl<HandleAbnormityMapper, HandleAbnormity> implements IHandleAbnormityService {
	@Override
    public long getHandleAbnormityCount(String tableId) {
        QueryWrapper<HandleAbnormity> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("takeout_id",tableId);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "HandleAbnormitys", allEntries = true)
    public HandleAbnormity saveHandleAbnormity(HandleAbnormity handleAbnormity) {
    	//首位添加人把谁什么时间加上
    	handleAbnormity.setDisposeUserId(MySysUser.id());
    	handleAbnormity.setDisposeTime(LocalDateTime.now());
    	//状态值 audit_status 
    	//未审核 audit_status_no
    	Integer audit_status_no = CacheUtils.keyDict.get("audit_status_no").getValue();
    	handleAbnormity.setAuditStatus(audit_status_no);
        baseMapper.insert(handleAbnormity);
        return handleAbnormity;
    }

    @Override
    public HandleAbnormity getHandleAbnormityById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "HandleAbnormitys", allEntries = true)
    public void updateHandleAbnormity(HandleAbnormity handleAbnormity) {
    	//这里就是异常得审批
    	//已审核 audit_status_yes
    	Integer audit_status_yes = CacheUtils.keyDict.get("audit_status_yes").getValue();
    	handleAbnormity.setAuditStatus(audit_status_yes);
    	handleAbnormity.setAuditUserId(MySysUser.id());
    	handleAbnormity.setAuditTime(LocalDateTime.now());
    	
        baseMapper.updateById(handleAbnormity);
        
    }

    
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "HandleAbnormitys", allEntries = true)
    public void deleteHandleAbnormity(HandleAbnormity handleAbnormity) {
        handleAbnormity.setDelFlag(true);
        baseMapper.updateById(handleAbnormity);
    }

    @Override
    @Cacheable("HandleAbnormitys")
    public List<HandleAbnormity> selectAll() {
        QueryWrapper<HandleAbnormity> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void backAudit(String id) {
		HandleAbnormity handleAbnormity = baseMapper.selectById(id);
		//未审核 audit_status_no
    	Integer audit_status_no = CacheUtils.keyDict.get("audit_status_no").getValue();
    	handleAbnormity.setAuditStatus(audit_status_no);
    	baseMapper.updateById(handleAbnormity);
	}


}
