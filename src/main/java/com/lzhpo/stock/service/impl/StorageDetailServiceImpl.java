package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.StorageDetail;
import com.lzhpo.stock.mapper.StorageDetailMapper;
import com.lzhpo.stock.service.IStorageDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 入库明细表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Service
public class StorageDetailServiceImpl extends ServiceImpl<StorageDetailMapper, StorageDetail> implements IStorageDetailService {
	@Override
    public long getStorageDetailCount(String name) {
        QueryWrapper<StorageDetail> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageDetails", allEntries = true)
    public StorageDetail saveStorageDetail(StorageDetail storageDetail) {
        baseMapper.insert(storageDetail);
        /**
	*预留编辑代码 
	*/
        return storageDetail;
    }

    @Override
    public StorageDetail getStorageDetailById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageDetails", allEntries = true)
    public void updateStorageDetail(StorageDetail storageDetail) {
        baseMapper.updateById(storageDetail);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageDetails", allEntries = true)
    public void deleteStorageDetail(StorageDetail storageDetail) {
        storageDetail.setDelFlag(true);
        baseMapper.updateById(storageDetail);
    }

    @Override
    @Cacheable("StorageDetails")
    public List<StorageDetail> selectAll() {
        QueryWrapper<StorageDetail> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public void deleteStorageDetailById(String storageId) {
		 QueryWrapper<StorageDetail> wrapper = new QueryWrapper<>();
	     wrapper.eq("storage_id",storageId);
	     baseMapper.delete(wrapper);
	}

	@Override
	public List<StorageDetail> selectStorageDetailByStorageId(String storageId) {
		 QueryWrapper<StorageDetail> wrapper = new QueryWrapper<>();
	     wrapper.eq("storage_id",storageId);
	     return baseMapper.selectList(wrapper);
	}

	@Override
	public Set<String> selectStorageIdsByMaterialId(String materialId) {
		QueryWrapper<StorageDetail> wrapper = new QueryWrapper<>();
		wrapper.eq("del_flag",false);
	    wrapper.eq("material_id",materialId);
	    List<StorageDetail> list = baseMapper.selectList(wrapper);
	    Set<String> str = new HashSet<>();
	    for (StorageDetail detail : list) {
	    	str.add(detail.getStorageId());
		}
		return str;
	}


}
