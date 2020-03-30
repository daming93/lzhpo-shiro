package com.lzhpo.client.service.impl;

import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.mapper.BasicdataMapper;
import com.lzhpo.client.service.IBasicdataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 客户表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-24
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BasicdataServiceImpl extends ServiceImpl<BasicdataMapper, Basicdata> implements IBasicdataService {
	@Override
    public long getBasicdataCount(String name) {
        QueryWrapper<Basicdata> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("clientName", name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "basicdatas", allEntries = true)
    public Basicdata saveBasicdata(Basicdata basicdata) {
        baseMapper.insert(basicdata);
        /**
	*预留编辑代码 
	*/
        return basicdata;
    }

    @Override
    public Basicdata getBasicdataById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "basicdatas", allEntries = true)
    public void updateBasicdata(Basicdata basicdata) {
        baseMapper.updateById(basicdata);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "basicdatas", allEntries = true)
    public void deleteBasicdata(Basicdata basicdata) {
        basicdata.setDelFlag(true);
        baseMapper.updateById(basicdata);
    }

    @Override
    @Cacheable("basicdatas")
    public List<Basicdata> selectAll() {
        QueryWrapper<Basicdata> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }
}
