package com.lzhpo.material.item.service.impl;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzhpo.material.item.entity.Category;
import com.lzhpo.material.item.mapper.CategoryMapper;
import com.lzhpo.material.item.service.ICategoryService;
/**
 * <p>
 * 物料分类表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {
	@Override
    public long getCategoryCount(String name) {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Categorys", allEntries = true)
    public Category saveCategory(Category category) {
        baseMapper.insert(category);
        /**
	*预留编辑代码 
	*/
        return category;
    }

    @Override
    public Category getCategoryById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Categorys", allEntries = true)
    public void updateCategory(Category category) {
        baseMapper.updateById(category);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Categorys", allEntries = true)
    public void deleteCategory(Category category) {
        category.setDelFlag(true);
        baseMapper.updateById(category);
    }

    @Override
    @Cacheable("Categorys")
    public List<Category> selectAll() {
        QueryWrapper<Category> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
