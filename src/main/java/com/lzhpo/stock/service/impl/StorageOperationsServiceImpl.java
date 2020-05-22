package com.lzhpo.stock.service.impl;

import com.lzhpo.stock.entity.StorageOperations;
import com.lzhpo.stock.mapper.StorageOperationsMapper;
import com.lzhpo.stock.service.IStorageOperationsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.transaction.annotation.Transactional;
/**
 * <p>
 * 入库操作表 服务实现类
 * </p>
 *
 * @author xdm
 * @since 2020-05-05
 */
@Service
public class StorageOperationsServiceImpl extends ServiceImpl<StorageOperationsMapper, StorageOperations> implements IStorageOperationsService {
	@Override
    public long getStorageOperationsCount(String name) {
        QueryWrapper<StorageOperations> wrapper = new QueryWrapper<>();
	// 下行编辑条件
        wrapper.eq("del_flag",false); 
       // wrapper.eq("name",name);
        return baseMapper.selectCount(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageOperationss", allEntries = true)
    public StorageOperations saveStorageOperations(StorageOperations storageOperations) {
        baseMapper.insert(storageOperations);
        /**
	*预留编辑代码 
	*/
        return storageOperations;
    }

    @Override
    public StorageOperations getStorageOperationsById(String id) {
        return baseMapper.selectById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageOperationss", allEntries = true)
    public void updateStorageOperations(StorageOperations storageOperations) {
        baseMapper.updateById(storageOperations);
        /**
	*预留编辑代码
	*/
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "StorageOperationss", allEntries = true)
    public void deleteStorageOperations(StorageOperations storageOperations) {
        storageOperations.setDelFlag(true);
        baseMapper.updateById(storageOperations);
    }

    @Override
    @Cacheable("StorageOperationss")
    public List<StorageOperations> selectAll() {
        QueryWrapper<StorageOperations> wrapper = new QueryWrapper<>();
        wrapper.eq("del_flag",false);
        return baseMapper.selectList(wrapper);
    }

	@Override
	public List<StorageOperations> selectByStorageId(String id) {
		 QueryWrapper<StorageOperations> wrapper = new QueryWrapper<>();
	     wrapper.eq("storage_id",id);
	     wrapper.orderByDesc("create_date");
	     List<StorageOperations> operations = baseMapper.selectList(wrapper);
	     return operations;
	}


}
