package com.lzhpo.sys.service.impl;

import com.lzhpo.sys.entity.Dictionary;
import com.lzhpo.sys.mapper.DictionaryMapper;
import com.lzhpo.sys.service.IDictionaryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 字典表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-03-26
 */
@Service("dictionaryService")
@Transactional(rollbackFor = Exception.class)
public class DictionaryServiceImpl extends ServiceImpl<DictionaryMapper, Dictionary> implements IDictionaryService {
	@Override
    public long getDictionaryCount(String key) {
        QueryWrapper<Dictionary> wrapper = new QueryWrapper<>();
	wrapper.eq("del_flag",false); 
	// 下行编辑条件
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictionarys", allEntries = true)
    public Dictionary saveDictionary(Dictionary dictionary) {
        baseMapper.insert(dictionary);
        /**
	*预留编辑代码 
	*/
        return dictionary;
    }

    @Override
    public Dictionary getDictionaryById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictionarys", allEntries = true)
    public void updateDictionary(Dictionary dictionary) {
        baseMapper.updateById(dictionary);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "dictionarys", allEntries = true)
    public void deleteDictionary(Dictionary dictionary) {
        dictionary.setDelFlag(true);
        baseMapper.updateById(dictionary);
    }

    @Override
    @Cacheable("dictionarys")
    public List<Dictionary> selectAll() {
        QueryWrapper<Dictionary> wrapper = new QueryWrapper<>();
        return baseMapper.selectList(wrapper);
    }
}
