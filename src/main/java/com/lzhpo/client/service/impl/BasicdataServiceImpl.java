package com.lzhpo.client.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.client.entity.Basicdata;
import com.lzhpo.client.mapper.BasicdataMapper;
import com.lzhpo.client.service.IBasicdataService;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
/**
 * <p>
 * 客户表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-24
 */
@Service("basicdataService")
@Transactional(rollbackFor = Exception.class)
public class BasicdataServiceImpl extends ServiceImpl<BasicdataMapper, Basicdata> implements IBasicdataService {
	@Override
    public long getBasicdataCount(String fieldName,String value) {
        QueryWrapper<Basicdata> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq(fieldName, value);
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
    	TimeInterval timer = DateUtil.timer();
        QueryWrapper<Basicdata> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        List<Basicdata> list = baseMapper.selectList(wrapper);
        System.out.println(timer.interval()+"service");
        return list;
    }
}
