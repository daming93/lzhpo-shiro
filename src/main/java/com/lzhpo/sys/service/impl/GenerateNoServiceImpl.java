package com.lzhpo.sys.service.impl;

import com.lzhpo.sys.entity.GenerateNo;
import com.lzhpo.sys.mapper.GenerateNoMapper;
import com.lzhpo.sys.service.IGenerateNoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 唯一编号 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
@Service("generateNoService")
@Transactional(rollbackFor = Exception.class)
public class GenerateNoServiceImpl extends ServiceImpl<GenerateNoMapper, GenerateNo> implements IGenerateNoService {
	@Override
    public long getGenerateNoCount(String prefix,String time) {
        QueryWrapper<GenerateNo> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("prefix",prefix);
        wrapper.eq("date_no",time);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "generateNos", allEntries = true)
    public GenerateNo saveGenerateNo(GenerateNo generateNo) {
        baseMapper.insert(generateNo);
        /**
	*预留编辑代码 
	*/
        return generateNo;
    }

    @Override
    public GenerateNo getGenerateNoById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "generateNos", allEntries = true)
    public void updateGenerateNo(GenerateNo generateNo) {
        baseMapper.updateById(generateNo);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "generateNos", allEntries = true)
    public void deleteGenerateNo(GenerateNo generateNo) {
        //generateNo.setDelFlag(true);
        baseMapper.updateById(generateNo);
    }

    @Override
    @Cacheable("generateNos")
    public List<GenerateNo> selectAll() {
        QueryWrapper<GenerateNo> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public GenerateNo selectGenerate(String prefix, String time) {
		QueryWrapper<GenerateNo> wrapper = new QueryWrapper<>();
		// 下行编辑条件
		wrapper.eq("prefix",prefix);
		wrapper.eq("date_no",time);
		GenerateNo entity = baseMapper.selectOne(wrapper);
		return entity;
	}

	@Override
	public synchronized String nextCode(String prefix) {
		String time = new SimpleDateFormat("yyyyMMdd").format(new Date());
		Long count = getGenerateNoCount(prefix, time);
		DecimalFormat df = new DecimalFormat("00000");
		if(count!=0){
			GenerateNo entity = selectGenerate(prefix, time);
			entity.setSerialNo(entity.getSerialNo()+1);
			updateGenerateNo(entity);
			return prefix+time+df.format(entity.getSerialNo());
		}else {
			GenerateNo entity = new GenerateNo();
			entity.setDateNo(time);
			entity.setPrefix(prefix);
			entity.setSerialNo(1);
			saveGenerateNo(entity);
			return prefix+time+"00001";
		}
	}
}
