package com.lzhpo.material.item.service.impl;

import com.lzhpo.material.item.entity.Brand;
import com.lzhpo.material.item.mapper.BrandMapper;
import com.lzhpo.material.item.service.IBrandService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 物料品牌表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-04-15
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements IBrandService {
	@Override
    public long getBrandCount(String name) {
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
        wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Brands", allEntries = true)
    public Brand saveBrand(Brand brand) {
        baseMapper.insert(brand);
        /**
	*预留编辑代码 
	*/
        return brand;
    }

    @Override
    public Brand getBrandById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Brands", allEntries = true)
    public void updateBrand(Brand brand) {
        baseMapper.updateById(brand);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "Brands", allEntries = true)
    public void deleteBrand(Brand brand) {
        brand.setDelFlag(true);
        baseMapper.updateById(brand);
    }

    @Override
    @Cacheable("Brands")
    public List<Brand> selectAll() {
        QueryWrapper<Brand> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }


}
