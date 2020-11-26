package com.lzhpo.customer.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.customer.entity.Abnormity;
import com.lzhpo.customer.mapper.AbnormityMapper;
import com.lzhpo.customer.service.IAbnormityService;
/**
 * <p>
 * 异常表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-06-09
 */
@Service
public class AbnormityServiceImpl extends ServiceImpl<AbnormityMapper, Abnormity> implements IAbnormityService {
	@Override
    public long getAbnormityCount(String cause) {
        QueryWrapper<Abnormity> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("cause",cause);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Abnormitys", allEntries = true)
    public Abnormity saveAbnormity(Abnormity abnormity) {
        baseMapper.insert(abnormity);
        /**
	*预留编辑代码 
	*/
        return abnormity;
    }

    @Override
    public Abnormity getAbnormityById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Abnormitys", allEntries = true)
    public void updateAbnormity(Abnormity abnormity) {
        baseMapper.updateById(abnormity);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Abnormitys", allEntries = true)
    public void deleteAbnormity(Abnormity abnormity) {
        abnormity.setDelFlag(true);
        baseMapper.updateById(abnormity);
    }

    @Override
    @Cacheable("Abnormitys")
    public List<Abnormity> selectAll() {
        QueryWrapper<Abnormity> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public List<Abnormity> selectAbnorityByType(String typeId) {
		QueryWrapper<Abnormity> wrapper = new QueryWrapper<>();
		wrapper.eq("abnormity_type_id", typeId);
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
	}


}
